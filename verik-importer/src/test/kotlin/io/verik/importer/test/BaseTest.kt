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

import io.verik.importer.main.ImporterContext
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.Platform
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.VerikImporterConfig
import io.verik.importer.message.MessageCollector
import io.verik.importer.parse.LexerStage
import io.verik.importer.preprocess.PreprocessorStage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths

abstract class BaseTest {

    fun driveMessageTest(content: String, isError: Boolean, message: String) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        if (isError) {
            val throwable = assertThrows<TestErrorException> {
                stageSequence.process(importerContext)
            }
            assertEquals(throwable.message, message)
        } else {
            val throwable = assertThrows<TestWarningException> {
                stageSequence.process(importerContext)
            }
            assertEquals(throwable.message, message)
        }
    }

    fun drivePreprocessorFragmentTest(content: String, expected: String) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        for (stage in stageSequence.stages) {
            stage.process(importerContext)
            if (stage is PreprocessorStage)
                break
        }
        val actual = importerContext.preprocessorFragments.joinToString(separator = "\n") { it.content }
        assertEquals(expected, actual)
    }

    fun driveLexerFragmentTest(content: String, expected: String) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        for (stage in stageSequence.stages) {
            stage.process(importerContext)
            if (stage is LexerStage)
                break
        }
        val actual = importerContext.lexerFragments.joinToString(separator = " ") { it.type.toString() }
        assertEquals(expected, actual)
    }

    private fun getImporterContext(content: String): ImporterContext {
        val config = getConfig()
        val importerContext = ImporterContext(config)
        importerContext.inputFileContexts[config.importedFiles[0]] = InputFileContext(content)
        return importerContext
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): VerikImporterConfig {
            val importedFile = if (Platform.isWindows) "C:\\src\\test.sv" else "/src/test.sv"
            return VerikImporterConfig(
                version = "local-SNAPSHOT",
                timestamp = "",
                projectName = "test",
                importedFiles = listOf(Paths.get(importedFile)),
                debug = true,
                suppressedWarnings = listOf(),
                promotedWarnings = listOf()
            )
        }
    }
}
