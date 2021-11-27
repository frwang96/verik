/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.plugin

import io.verik.importer.main.VerikImporterConfig
import org.gradle.api.Project
import java.nio.file.Path

object VerikImporterConfigBuilder {

    fun getConfig(project: Project, extension: VerikImporterPluginExtension): VerikImporterConfig {
        return VerikImporterConfig(
            version = VerikConfigUtil.getVersion(project),
            timestamp = VerikConfigUtil.getTimestamp(),
            projectName = project.name,
            projectDir = project.projectDir.toPath(),
            importedFiles = extension.importedFiles,
            debug = extension.debug,
            suppressedWarnings = extension.suppressedWarnings,
            promotedWarnings = extension.promotedWarnings
        )
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik-import").toPath()
    }
}
