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

import org.apache.commons.compress.compressors.CompressorException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.annotation.Nonnull;

import io.sigpipe.jbsdiff.InvalidHeaderException;
import io.sigpipe.jbsdiff.Patch;

/**
 * Provides a cacheable artifact for game files which have been patched using a Flange patch file.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class PatchedCacheableArtifact implements CacheableArtifact {
    public static final byte[] MAGIC_NUMBER = new byte[]{(byte) 0xDE, (byte) 0xFE, (byte) 0xC8, (byte) 0xED};

    private final String name;
    private final CacheableArtifact diff;
    private final VersionCache cache;
    private final Path cachePath;

    public PatchedCacheableArtifact(@Nonnull String name, @Nonnull CacheableArtifact diff, @Nonnull VersionCache cache, @Nonnull Path cachePath) {
        this.name = name;
        this.diff = diff;
        this.cache = cache;
        this.cachePath = cachePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCached() {
        return Files.exists(this.cachePath);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Path getCachePath() throws IllegalStateException {
        if (!this.isCached()) {
            throw new IllegalStateException("Basin version is not available from cache: " + this.toString());
        }

        return this.cachePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateCache() throws IOException {
        if (this.isCached()) {
            return;
        }

        this.diff.populateCache();

        try (FileChannel inputChannel = FileChannel.open(this.diff.getCachePath(), StandardOpenOption.READ)) {
            ByteBuffer fileHeader = ByteBuffer.allocate(MAGIC_NUMBER.length + 4);
            inputChannel.read(fileHeader);

            {
                byte[] magicNumber = new byte[MAGIC_NUMBER.length];
                fileHeader.get(magicNumber);

                if (!Arrays.equals(MAGIC_NUMBER, magicNumber)) {
                    throw new IOException("Invalid or corrupted patch: Magic number mismatch");
                }
            }

            final String gameVersion;
            {
                int gameVersionLength = fileHeader.getInt();
                ByteBuffer gameVersionBuffer = ByteBuffer.allocate(gameVersionLength);
                inputChannel.read(gameVersionBuffer);

                gameVersion = new String(gameVersionBuffer.array(), StandardCharsets.UTF_8);
            }

            CacheableArtifact gameArtifact = this.cache.getGameArtifact(gameVersion);
            gameArtifact.populateCache();

            byte[] fileBytes = Files.readAllBytes(gameArtifact.getCachePath());
            byte[] patchBytes;
            {
                ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
                inputChannel.read(lengthBuffer);

                patchBytes = new byte[lengthBuffer.getInt()];

                ByteBuffer patchBuffer = ByteBuffer.allocate(patchBytes.length);
                inputChannel.read(patchBuffer);

                patchBytes = patchBuffer.array();
            }

            try (FileChannel outputChannel = FileChannel.open(this.cachePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                try (OutputStream outputStream = Channels.newOutputStream(outputChannel)) {
                    Patch.patch(fileBytes, patchBytes, outputStream);
                }
            }
        } catch (CompressorException ex) {
            throw new IOException("Illegal patch compression: " + ex.getMessage(), ex);
        } catch (InvalidHeaderException ex) {
            throw new IOException("Malformed binary patch: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("PatchedCacheableArtifact{name=\"%s\",diff=%s,cachePath=\"%s\"}", this.name, this.diff, this.cachePath);
    }
}
