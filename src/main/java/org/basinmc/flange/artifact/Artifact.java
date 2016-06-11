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
import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 * Represents a single downloadable artifact.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Artifact {

    /**
     * Downloads the artifact to a local path.
     *
     * @param path a path.
     * @throws IOException when downloading fails.
     */
    void download(@Nonnull Path path) throws IOException;

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
