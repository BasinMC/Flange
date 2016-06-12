/*
 * Copyright 2016 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.basinmc.flange.artifact;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class TeamCityArtifactRepository implements ArtifactRepository {
    public static final String ARTIFACT_LIST_PATTERN = "/guestAuth/app/rest/buildTypes/id:%s/builds?locator=status:SUCCESS";
    public static final String ARTIFACT_BUILD_PATTERN = "/guestAuth/app/rest/builds/id:%d/artifacts/children/";

    private final String baseUrl;
    private final String buildType;

    public TeamCityArtifactRepository(@Nonnull String baseUrl, @Nonnull String buildType) {
        this.baseUrl = baseUrl;
        this.buildType = buildType;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Optional<Artifact> findArtifact(@Nonnull String name) throws IOException {
        if (!name.startsWith("#")) {
            return Optional.empty();
        }

        name = name.substring(1);

        try {
            long buildId = Integer.parseUnsignedInt(name);
            URL url = new URL(this.getUrl(String.format(ARTIFACT_BUILD_PATTERN, buildId)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != 200) {
                return Optional.empty();
            }

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(inputStream);

                document.getDocumentElement().normalize();

                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xPath = xPathFactory.newXPath();

                XPathExpression filesExpression = xPath.compile("/files/file");
                NodeList nodes = (NodeList) filesExpression.evaluate(document, XPathConstants.NODESET);

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    String fileName = node.getAttributes().getNamedItem("name").getTextContent();

                    if (!fileName.endsWith(".diff")) {
                        continue;
                    }

                    String artifactUrl = this.getUrl(node.getAttributes().getNamedItem("href").getTextContent());
                    return Optional.of(new RemoteArtifact("#" + name, false, new URL(artifactUrl)));
                }

                return Optional.empty();
            }
        } catch (NumberFormatException ex) {
            return Optional.empty();
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            throw new RuntimeException("Could not parse build server response: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Set<ArtifactMetadata> getArtifactList() throws IOException {
        try {
            URL url = new URL(this.getUrl(String.format(ARTIFACT_LIST_PATTERN, this.buildType)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != 200) {
                return Collections.emptySet();
            }

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(inputStream);

                document.getDocumentElement().normalize();

                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xPath = xPathFactory.newXPath();

                XPathExpression buildsExpression = xPath.compile("/build/builds");
                NodeList nodes = (NodeList) buildsExpression.evaluate(document, XPathConstants.NODESET);

                Set<ArtifactMetadata> artifacts = new HashSet<>();

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);

                    artifacts.add(new SimpleArtifactMetadata("#" + node.getAttributes().getNamedItem("number").getTextContent(), false));
                }

                return artifacts;
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            throw new RuntimeException("Could not parse build server response: " + ex.getMessage());
        }
    }

    /**
     * Retrieves a full build server url.
     *
     * @param endpoint an endpoint.
     * @return a full url.
     */
    @Nonnull
    private String getUrl(@Nonnull String endpoint) {
        return this.baseUrl + endpoint;
    }
}
