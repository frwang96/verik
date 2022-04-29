/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.test

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.common.ElementPrinter
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.OutputContext
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.main.SourceSetConfig
import io.verik.compiler.main.SourceSetContext
import io.verik.compiler.main.StageSequencer
import io.verik.compiler.main.VerikCompilerConfig
import io.verik.compiler.message.MessageCollector
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths
import kotlin.reflect.KClass

abstract class BaseTest {

    fun driveMessageTest(
        @Language("kotlin") content: String,
        isError: Boolean,
        message: String
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        if (isError) {
            val throwable = assertThrows<TestErrorException> {
                stageSequence.processAll(projectContext)
            }
            assertEquals(message, throwable.message)
        } else {
            val throwable = assertThrows<TestWarningException> {
                stageSequence.processAll(projectContext)
            }
            assertEquals(message, throwable.message)
        }
    }

    fun <S : ProjectStage> driveElementTest(
        @Language("kotlin") content: String,
        stageClass: KClass<S>,
        expected: String,
        selector: (EProject) -> Any
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, stageClass)
        val selected = selector(projectContext.project)
        if (selected is List<*>) {
            val elements = selected.map { it as EElement }
            assertElementEquals(
                getExpectedString(expected),
                elements.joinToString(prefix = "[", postfix = "]") { ElementPrinter.dump(it) }
            )
        } else {
            val element = selected as EElement
            assertElementEquals(getExpectedString(expected), ElementPrinter.dump(element))
        }
    }

    fun driveTextFileTest(
        @Language("kotlin") content: String,
        expected: String,
        selector: (OutputContext) -> TextFile
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processAll(projectContext)
        val textFile = selector(projectContext.outputContext)
        assertOutputTextEquals(expected, textFile)
    }

    internal fun getProjectContext(@Language("kotlin") content: String): ProjectContext {
        val config = getConfig()
        val contentWithPackageHeader = """
            @file:Verik
            package test
            import io.verik.core.*
            $content
        """.trimIndent()
        val projectContext = ProjectContext(config)
        val textFile = TextFile(config.sourceSetConfigs[0].files[0], contentWithPackageHeader)
        val sourceSetContext = SourceSetContext(config.sourceSetConfigs[0].name, listOf(textFile))
        projectContext.sourceSetContexts = listOf(sourceSetContext)
        return projectContext
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

    internal fun assertOutputTextEquals(expected: String, actual: TextFile) {
        val expectedLines = expected.lines()
            .dropLastWhile { it.isEmpty() }
        val actualLines = actual.content.lines()
            .let { lines ->
                val index = lines.indexOfFirst { it.startsWith("// Output") } + 2
                lines.subList(index, lines.size)
            }
            .dropLastWhile { it.isEmpty() }

        assertEquals(
            expectedLines.joinToString(separator = "\n"),
            actualLines.joinToString(separator = "\n")
        )
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): VerikCompilerConfig {
            val buildDir = Paths.get("/build/verik")
            val projectFile = Paths.get("/src/main/kotlin/test/Test.kt")
            val sourceSetConfig = SourceSetConfig("test", listOf(projectFile))
            return VerikCompilerConfig(
                toolchain = "verik",
                timestamp = "",
                projectName = "test",
                buildDir = buildDir,
                sourceSetConfigs = listOf(sourceSetConfig),
                timescale = "1ns / 1ns",
                entryPoints = listOf(),
                enableDeadCodeElimination = false,
                labelLines = false,
                indentLength = 4,
                wrapLength = 80,
                suppressedWarnings = listOf("KOTLIN_COMPILE_WARNING"),
                promotedWarnings = listOf(),
                maxErrorCount = 0,
                debug = true
            )
        }
    }
}
