/*
 * Copyright 2020 Francis Wang
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

package verik.core.main.config

import verik.core.base.SymbolContext
import java.io.File

enum class CompileScopeType {
    TOP,
    ALL;

    companion object {

        operator fun invoke(string: String?): CompileScopeType {
            return when (string) {
                null -> TOP
                "top" -> TOP
                "all" -> ALL
                else -> throw java.lang.IllegalArgumentException("illegal compile scope $string")
            }
        }
    }
}

data class ProjectConfig(
        val timeString: String,
        val configFile: File,
        val projectDir: File,
        val project: String,
        val buildDir: File,
        val buildCopyDir: File,
        val buildOutDir:File,
        val gradle: ProjectGradleConfig,
        val compile: ProjectCompileConfig,
        val stubs: ProjectStubsConfig?,
        val symbolContext: SymbolContext
) {

    val configCopy = buildDir.resolve("vkproject.yaml")
    val orderFile = buildDir.resolve("order.txt")
    val stubsFile = buildDir.resolve("stubs.txt")

    companion object {

        operator fun invoke(configPath: String): ProjectConfig {
            return ConfigLoader.loadProjectConfig(configPath)
        }
    }
}

data class ProjectGradleConfig(
        val dir: File,
        val wrapperSh: File,
        val wrapperBat: File
)

data class ProjectCompileConfig(
        val top: String?,
        val scopeType: CompileScopeType,
        val labelLines: Boolean
)

data class ProjectStubsConfig(
        val main: String,
        val jar: File
)
