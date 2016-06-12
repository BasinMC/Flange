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
package org.basinmc.flange.artifact.repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides an artifact implementation which relies on an HTTP url.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class RemoteArtifact extends SimpleArtifactMetadata implements Artifact {
    private final URL url;

    public RemoteArtifact(@Nonnull String identifier, @Nonnull String name, boolean stable, @Nonnull URL url) {
        super(identifier, name, stable);
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @WillNotClose
    public ReadableByteChannel openChannel() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Could not retrieve artifact: Expected status 200 but received " + connection.getResponseCode());
        }

        return Channels.newChannel(connection.getInputStream());
    }
}
