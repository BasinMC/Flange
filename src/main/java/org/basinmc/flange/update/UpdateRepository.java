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
package org.basinmc.flange.update;

import org.basinmc.flange.version.CacheableArtifact;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Provides a common interface for fetching a set of known versions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface UpdateRepository {

    /**
     * Retrieves a specific artifact from the repository.
     *
     * @param revision a repository unique revision identifier.
     * @return an artifact or, if no artifact with such name is present, an empty optional.
     *
     * @throws IOException when accessing the repository fails.
     */
    @Nonnull
    Optional<CacheableArtifact> getArtifact(@Nonnull String revision) throws IOException;

    /**
     * Retrieves the latest available artifact in this repository.
     *
     * @return an artifact or, if no artifacts are present, an empty optional.
     *
     * @throws IOException when accessing the repository fails.
     */
    @Nonnull
    Optional<CacheableArtifact> getLatestArtifact() throws IOException;

    /**
     * Retrieves a list of valid artifact revisions in the repository.
     *
     * @return a set of revisions.
     *
     * @throws IOException when accessing the repository fails.
     */
    @Nonnull
    Set<String> getRevisions() throws IOException;
}
