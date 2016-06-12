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

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface ArtifactMetadata {

    /**
     * Retrieves a machine understandable name (such as a version number).
     * @return an identifier.
     */
    @Nonnull
    String getIdentifier();

    /**
     * Retrieves a human readable name for the artifact.
     *
     * @return a name.
     */
    @Nonnull
    String getName();

    /**
     * Checks whether the artifact is considered stable.
     *
     * @return true if stable.
     */
    boolean isStable();
}
