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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.annotation.Nonnull;
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

    public RemoteArtifact(@Nonnull String name, boolean stable, @Nonnull URL url) {
        super(name, stable);
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void download(@Nonnull Path path) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Could not retrieve artifact: Expected status 200 but received " + connection.getResponseCode());
        }

        try (InputStream inputStream = connection.getInputStream()) {
            try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
                try (FileChannel outputChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                    outputChannel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
                }
            }
        }
    }
}
