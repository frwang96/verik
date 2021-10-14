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

package io.verik.compiler.util

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.common.ElementPrinter
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.Config
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.StageSequencer
import io.verik.compiler.main.TextFile
import io.verik.compiler.message.MessageCollector
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Paths
import kotlin.reflect.KClass

abstract class BaseTest {

    fun <T : ProjectStage> driveTest(stageClass: KClass<T>, @Language("kotlin") content: String): ProjectContext {
        val config = getConfig()
        val contentWithPackageHeader = """
            package verik
            import io.verik.core.*
            $content
        """.trimIndent()
        val textFile = TextFile(config.projectFiles[0], contentWithPackageHeader)
        val projectContext = ProjectContext(config)
        projectContext.inputTextFiles = listOf(textFile)

        val stageSequence = StageSequencer.getStageSequence()
        assert(stageSequence.contains(stageClass))
        for (it in stageSequence.stages) {
            it.accept(projectContext)
            if (it::class == stageClass)
                break
        }

        return projectContext
    }

    fun assertElementEquals(expected: String, actual: EElement) {
        assertElementEquals(expected, ElementPrinter.dump(actual))
    }

    fun assertElementEquals(expected: String, actual: List<EElement>) {
        val actualString = actual.joinToString(prefix = "[", postfix = "]") { ElementPrinter.dump(it) }
        assertElementEquals(expected, actualString)
    }

    private fun assertElementEquals(expected: String, actualString: String) {
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
        val expectedString = expectedBuilder.toString()

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

    fun assertOutputTextEquals(expected: String, actual: TextFile) {
        val expectedLines = expected.lines()
            .dropLastWhile { it.isEmpty() }
        val actualLines = actual.content.lines()
            .let { lines ->
                val index = lines.indexOfFirst { it == "`endif" } + 2
                lines.subList(index, lines.size)
            }
            .dropLastWhile { it.isEmpty() }

        Assertions.assertEquals(expectedLines, actualLines)
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): Config {
            val projectDir = if (Platform.isWindows) "C:\\" else "/"
            val buildDir = if (Platform.isWindows) "C:\\build\\verik" else "/build/verik"
            val projectFile = if (Platform.isWindows) {
                "C:\\src\\main\\kotlin\\verik\\Test.kt"
            } else {
                "/src/main/kotlin/verik/Test.kt"
            }
            return Config(
                version = "local-SNAPSHOT",
                timestamp = "",
                projectName = "verik",
                projectDir = Paths.get(projectDir),
                buildDir = Paths.get(buildDir),
                projectFiles = listOf(Paths.get(projectFile)),
                debug = true,
                suppressedWarnings = listOf("KOTLIN_COMPILE_WARNING"),
                promotedWarnings = listOf(),
                maxErrorCount = 0,
                labelLines = false,
                wrapLength = 80,
                indentLength = 4,
                enableDeadCodeElimination = false
            )
        }
    }
}
