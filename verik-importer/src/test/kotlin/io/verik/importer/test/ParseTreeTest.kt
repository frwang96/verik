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

package io.verik.importer.test

import io.verik.importer.common.ParseTreePrinter
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import io.verik.importer.main.Platform
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.VerikImporterConfig
import java.nio.file.Paths

abstract class ParseTreeTest {

    fun driveTest(content: String) {
        val config = getConfig()
        val importerContext = ImporterContext(config)
        val textFile = TextFile(config.importedFiles[0], content)
        importerContext.importedTextFiles = listOf(textFile)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.process(importerContext)
        ParseTreePrinter.dump(importerContext.importedParseTrees[0])
    }

    private fun getConfig(): VerikImporterConfig {
        val importedFile = if (Platform.isWindows) "C:\\src\\test.sv" else "/src/test.sv"
        return VerikImporterConfig(
            version = "local-SNAPSHOT",
            timestamp = "",
            projectName = "test",
            importedFiles = listOf(Paths.get(importedFile)),
            debug = true
        )
    }
}
