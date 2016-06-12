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
import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 * Provides a base interface for cacheable objects.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface CacheableArtifact {

    /**
     * Checks whether the artifact has already been cached.
     *
     * @return true if cached, false otherwise.
     */
    boolean isCached();

    /**
     * Retrieves the path to the cached version of this artifact.
     *
     * @return a path pointing at the cached artifact.
     *
     * @throws IllegalStateException when the artifact is not available from the local cache.
     */
    @Nonnull
    Path getCachePath() throws IllegalStateException;

    /**
     * Populates the local cache for this artifact.
     *
     * @throws IOException when retrieving the artifact or writing to the cache fails.
     */
    void populateCache() throws IOException;
}
