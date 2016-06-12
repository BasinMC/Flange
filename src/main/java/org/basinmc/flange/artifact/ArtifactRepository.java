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

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Provides an interface to list and otherwise lookup artifacts.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface ArtifactRepository {

    /**
     * Attempts to find a specific artifact within the repository.
     *
     * @param name an artifact name.
     * @return an artifact or, if no such artifact could be found, an empty optional.
     *
     * @throws IOException when the repository is unavailable.
     */
    @Nonnull
    Optional<Artifact> findArtifact(@Nonnull String name) throws IOException;

    /**
     * Retrieves a full list of artifacts.
     *
     * @return a list of artifacts.
     *
     * @throws IOException when the repository is unavailable.
     */
    @Nonnull
    Set<ArtifactMetadata> getArtifactList() throws IOException;
}
