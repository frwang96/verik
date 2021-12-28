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

import io.verik.importer.antlr.SystemVerilogLexer
import io.verik.importer.ast.element.ECompilationUnit
import io.verik.importer.ast.element.EElement
import io.verik.importer.common.ElementPrinter
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import io.verik.importer.main.ImporterStage
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.Platform
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.VerikImporterConfig
import io.verik.importer.message.MessageCollector
import io.verik.importer.parse.ParserStage
import io.verik.importer.preprocess.PreprocessorStage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths
import kotlin.reflect.KClass

abstract class BaseTest {

    fun driveMessageTest(content: String, isError: Boolean, message: String) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        if (isError) {
            val throwable = assertThrows<TestErrorException> {
                stageSequence.process(importerContext)
            }
            assertEquals(message, throwable.message)
        } else {
            val throwable = assertThrows<TestWarningException> {
                stageSequence.process(importerContext)
            }
            assertEquals(message, throwable.message)
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
            if (stage is ParserStage)
                break
            stage.process(importerContext)
        }
        val actual = importerContext.lexerFragments.joinToString(separator = " ") {
            SystemVerilogLexer.VOCABULARY.getSymbolicName(it.type)
        }
        assertEquals(expected, actual)
    }

    fun <S : ImporterStage> driveElementTest(
        content: String,
        stageClass: KClass<S>,
        expected: String,
        selector: (ECompilationUnit) -> EElement
    ) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        assert(stageSequence.contains(stageClass))
        for (stage in stageSequence.stages) {
            stage.process(importerContext)
            if (stage::class == stageClass)
                break
        }
        val element = selector(importerContext.compilationUnit)
        assertElementEquals(getExpectedString(expected), ElementPrinter.dump(element))
    }

    fun driveTextFileTest(content: String, expected: String) {
        val importerContext = getImporterContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.process(importerContext)
        val textFile = when (importerContext.outputContext.packageTextFiles.size) {
            0 -> throw IllegalArgumentException("No package text files found")
            1 -> importerContext.outputContext.packageTextFiles[0]
            else -> throw IllegalArgumentException("Multiple package text files found")
        }

        val expectedLines = expected.lines()
            .dropLastWhile { it.isEmpty() }
        val actualLines = textFile.content.lines()
            .let { lines ->
                val index = lines.indexOfLast { it.startsWith("import ") } + 2
                lines.subList(index, lines.size)
            }
            .dropLastWhile { it.isEmpty() }

        assertEquals(
            expectedLines.joinToString(separator = "\n"),
            actualLines.joinToString(separator = "\n")
        )
    }

    internal fun getImporterContext(content: String): ImporterContext {
        val config = getConfig()
        val importerContext = ImporterContext(config)
        val textFile = TextFile(config.importedFiles[0], content)
        importerContext.inputFileContexts = listOf(InputFileContext(textFile))
        return importerContext
    }

    private fun getExpectedString(expected: String): String {
        val leftRoundBracketCount = expected.count { it == '(' }
        val rightRoundBracketCount = expected.count { it == ')' }
        val leftSquareBracketCount = expected.count { it == '[' }
        val rightSquareBracketCount = expected.count { it == ']' }
        assert(leftRoundBracketCount <= rightRoundBracketCount) { "Missing right round bracket" }
        assert(leftRoundBracketCount >= rightRoundBracketCount) { "Missing left round bracket" }
        assert(leftSquareBracketCount <= rightSquareBracketCount) { "Missing right square bracket" }
        assert(leftSquareBracketCount >= rightSquareBracketCount) { "Missing left square bracket" }

        val expectedBuilder = StringBuilder()
        expected.forEach {
            when (it) {
                in listOf(' ', '\n', '\t') -> {}
                ',' -> expectedBuilder.append(", ")
                else -> expectedBuilder.append(it)
            }
        }
        return expectedBuilder.toString()
    }

    private fun assertElementEquals(expectedString: String, actualString: String) {
        var expectedIndex = 0
        var actualIndex = 0
        var match = true
        while (expectedIndex < expectedString.length) {
            val char = expectedString[expectedIndex]
            val previousIsBackTick = expectedString.getOrNull(expectedIndex - 1) == '`'
            val nextIsBackTick = expectedString.getOrNull(expectedIndex + 1) == '`'
            val isWildcard = (char == '*') && !(previousIsBackTick && nextIsBackTick)
            if (isWildcard) {
                val previousIsBracket = expectedString.getOrNull(expectedIndex - 1) in listOf('(', '[')
                val nextIsBracket = expectedString.getOrNull(expectedIndex + 1) in listOf(')', ']')
                val isBracketedWildcard = previousIsBracket && nextIsBracket
                var bracketDepth = 0
                while (actualIndex < actualString.length) {
                    when (actualString[actualIndex]) {
                        '(' -> bracketDepth++
                        ')' -> if (--bracketDepth == -1) break
                        '[' -> bracketDepth++
                        ']' -> if (--bracketDepth == -1) break
                        ',' -> if (bracketDepth == 0 && !isBracketedWildcard) break
                    }
                    actualIndex++
                }
            } else {
                if (char != actualString[actualIndex]) {
                    match = false
                    break
                } else {
                    actualIndex++
                }
            }
            expectedIndex++
        }
        if (actualIndex != actualString.length)
            match = false
        assert(match) {
            "expected: <$expectedString> but was: <$actualString>"
        }
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): VerikImporterConfig {
            val buildDir = if (Platform.isWindows) "C:\\build\\verik-import" else "/build/verik-import"
            val importedFile = if (Platform.isWindows) "C:\\src\\test.sv" else "/src/test.sv"
            return VerikImporterConfig(
                toolchain = "verik",
                timestamp = "",
                projectName = "test",
                buildDir = Paths.get(buildDir),
                importedFiles = listOf(Paths.get(importedFile)),
                enablePreprocessorOutput = false,
                annotateDeclarations = false,
                suppressedWarnings = listOf(),
                promotedWarnings = listOf(),
                debug = true
            )
        }
    }
}
