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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 * Manages previously downloaded game versions, diffs and patched versions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class VersionCache {
    private static final String VERSION_PATTERN = "https://s3.amazonaws.com/Minecraft.Download/versions/%1$s/minecraft_server.%1$s.jar";
    private final Path cacheRoot;

    public VersionCache(@Nonnull Path cacheRoot) {
        this.cacheRoot = cacheRoot;
    }

    /**
     * Retrieves a cacheable artifact which represents a specific game version.
     *
     * @param version a game version.
     * @return an artifact.
     */
    @Nonnull
    public CacheableArtifact getGameArtifact(@Nonnull String version) {
        // TODO: This needs to be replaced by launcher meta as this method is deprecated (am sorry Dinnerbone ... I was too lazy <3)
        try {
            Files.createDirectories(this.cacheRoot.resolve("vanilla"));
            return new RemoteCacheableArtifact(version, this.cacheRoot.resolve("vanilla").resolve(version + ".jar"), new URL(String.format(VERSION_PATTERN, version)));
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid game version: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new RuntimeException("Could not access cache directory: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves a cacheable patch artifact.
     *
     * @param version   a unique patch version.
     * @param remoteUrl a remote URL to fetch the patch from.
     * @return an artifact.
     */
    @Nonnull
    public CacheableArtifact getPatchArtifact(@Nonnull String version, @Nonnull URL remoteUrl) {
        try {
            Files.createDirectories(this.cacheRoot.resolve("patch"));
            return new RemoteCacheableArtifact(version, this.cacheRoot.resolve("patch").resolve(version + ".fldiff"), remoteUrl);
        } catch (IOException ex) {
            throw new RuntimeException("Could not access cache directory: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves a patched artifact.
     *
     * @param version       a unique mod version.
     * @param patchArtifact a patch artifact to apply.
     * @return an artifact.
     */
    @Nonnull
    public CacheableArtifact getPatchedArtifact(@Nonnull String version, @Nonnull CacheableArtifact patchArtifact) {
        try {
            Files.createDirectories(this.cacheRoot.resolve("modded"));
            return new PatchedCacheableArtifact(version, patchArtifact, this, this.cacheRoot.resolve("modded").resolve(version + ".jar"));
        } catch (IOException ex) {
            throw new RuntimeException("Could not access cache directory: " + ex.getMessage(), ex);
        }
    }
}
