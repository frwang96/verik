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

package verik.core.main

import verik.core.assertStringEquals
import verik.core.config.CompileScopeType
import verik.core.config.ProjectCompileConfig
import verik.core.config.ProjectConfig
import verik.core.config.ProjectGradleConfig
import verik.core.symbol.SymbolContext
import org.junit.jupiter.api.Test
import java.io.File

internal class FileHeaderBuilderTest {

    @Test
    fun `strip header`() {
        val config = ProjectConfig(
                "",
                File(""),
                File(""),
                "",
                File(""),
                File(""),
                File(""),
                ProjectGradleConfig(File(""), File(""), File("")),
                ProjectCompileConfig(null, CompileScopeType.TOP, true),
                null,
                SymbolContext()
        )
        val header = FileHeaderBuilder.build(config, File(""), File(""))
        val fileString = "$header\nbody"
        assertStringEquals(FileHeaderBuilder.strip(fileString), "body")
    }
}