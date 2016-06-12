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
package org.basinmc.flange.version;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.annotation.Nonnull;

/**
 * Represents a cached or remotely available artifact.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class RemoteCacheableArtifact implements CacheableArtifact {
    private final String name;
    private final Path cacheFilePath;
    private final URL remoteLocation;

    public RemoteCacheableArtifact(@Nonnull String name, @Nonnull Path cacheFilePath, @Nonnull URL remoteLocation) {
        this.name = name;
        this.cacheFilePath = cacheFilePath;
        this.remoteLocation = remoteLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCached() {
        return Files.exists(this.cacheFilePath);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Path getCachePath() throws IllegalStateException {
        if (!this.isCached()) {
            throw new IllegalStateException("Game version is not available from cache: " + this.toString());
        }

        return this.cacheFilePath;
    }

    /**
     * Retrieves the human readable version name.
     *
     * @return a version name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateCache() throws IOException {
        if (this.isCached()) {
            return;
        }

        HttpURLConnection connection = (HttpURLConnection) this.remoteLocation.openConnection();

        if (connection.getResponseCode() != 200) {
            throw new IOException("Could not retrieve game version " + this.name + ": Expected response code 200 but got " + connection.getResponseCode());
        }

        try (InputStream inputStream = connection.getInputStream()) {
            try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
                try (FileChannel outputChannel = FileChannel.open(this.cacheFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE)) {
                    outputChannel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("RemoteCacheableArtifact{name=\"%s\",cachePath=\"%s\",cached=%s}", this.name, this.cacheFilePath.toAbsolutePath(), this.isCached());
    }
}
