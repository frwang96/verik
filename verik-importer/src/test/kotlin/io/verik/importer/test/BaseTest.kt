/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.test

import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.cast.common.CasterStage
import io.verik.importer.common.ElementPrinter
import io.verik.importer.common.TextFile
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.VerikImporterConfig
import io.verik.importer.message.MessageCollector
import io.verik.importer.preprocess.PreprocessorSerializerStage
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.tree.RuleNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths
import kotlin.reflect.KClass

abstract class BaseTest {

    private val DOCSTRING_REGEX = Regex("(\\s*/\\*\\*.*|\\s*\\*.*)")

    fun driveMessageTest(content: String, isError: Boolean, message: String) {
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

    fun drivePreprocessorTest(content: String, expected: String) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, PreprocessorSerializerStage::class)
        val preprocessorTextFile = projectContext.outputContext.preprocessorTextFile!!
        assertEquals(
            expected.trim(),
            preprocessorTextFile.content.trim()
        )
    }

    fun driveCasterTest(
        ruleContextClass: KClass<out RuleContext>,
        content: String,
        expected: String,
        selector: (EProject) -> EElement
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, CasterStage::class)
        val ruleContextCheckerVisitor = RuleContextCheckerVisitor(ruleContextClass)
        projectContext.inputFileContexts.forEach {
            it.ruleContext.accept(ruleContextCheckerVisitor)
        }
        assertTrue(ruleContextCheckerVisitor.hasRuleContext) {
            "Rule context not found: ${ruleContextClass.simpleName}"
        }
        val element = selector(projectContext.project)
        assertElementEquals(getExpectedString(expected), ElementPrinter.dump(element))
    }

    fun driveSignatureTest(
        ruleContextClass: KClass<out RuleContext>,
        content: String,
        expected: String,
        selector: (EProject) -> EDeclaration
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, CasterStage::class)
        val ruleContextCheckerVisitor = RuleContextCheckerVisitor(ruleContextClass)
        projectContext.inputFileContexts.forEach {
            it.ruleContext.accept(ruleContextCheckerVisitor)
        }
        assertTrue(ruleContextCheckerVisitor.hasRuleContext) {
            "Rule context not found: ${ruleContextClass.simpleName}"
        }
        val declaration = selector(projectContext.project)
        assertEquals(expected, declaration.signature)
    }

    fun <S : ProjectStage> driveElementTest(
        content: String,
        stageClass: KClass<S>,
        expected: String,
        selector: (EProject) -> EElement
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, stageClass)
        val element = selector(projectContext.project)
        assertElementEquals(getExpectedString(expected), ElementPrinter.dump(element))
    }

    fun driveTextFileTest(content: String, expected: String) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processAll(projectContext)
        val textFile = when (projectContext.outputContext.sourceTextFiles.size) {
            0 -> throw IllegalArgumentException("No source text files found")
            1 -> projectContext.outputContext.sourceTextFiles[0]
            else -> throw IllegalArgumentException("Multiple source text files found")
        }

        val expectedLines = expected.lines()
            .dropLastWhile { it.isEmpty() }
        val actualLines = textFile.content.lines()
            .let { lines ->
                val index = lines.indexOfLast { it.startsWith("import ") } + 2
                lines.subList(index, lines.size)
            }
            .filter { !it.matches(DOCSTRING_REGEX) }
            .dropLastWhile { it.isEmpty() }

        assertEquals(
            expectedLines.joinToString(separator = "\n"),
            actualLines.joinToString(separator = "\n")
        )
    }

    private fun getProjectContext(content: String): ProjectContext {
        val config = getConfig()
        val projectContext = ProjectContext(config)
        val textFile = TextFile(config.importedFiles[0], content)
        val inputFileContext = InputFileContext(textFile)
        projectContext.inputFileContexts = listOf(inputFileContext)
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

    private class RuleContextCheckerVisitor(
        private val ruleContextClass: KClass<out RuleContext>
    ) : SystemVerilogParserBaseVisitor<Unit>() {

        var hasRuleContext = false

        override fun visitChildren(node: RuleNode?) {
            if (hasRuleContext)
                return
            super.visitChildren(node)
            if (ruleContextClass.isInstance(node)) {
                hasRuleContext = true
            }
        }
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): VerikImporterConfig {
            val buildDir = Paths.get("/build/verik-import")
            val importedFile = Paths.get("/src/test.sv")
            return VerikImporterConfig(
                toolchain = "verik",
                timestamp = "",
                projectName = "test",
                buildDir = buildDir,
                importedFiles = listOf(importedFile),
                includeDirs = listOf(),
                enablePreprocessorOutput = true,
                suppressedWarnings = listOf(),
                promotedWarnings = listOf(),
                maxErrorCount = 0,
                debug = true
            )
        }
    }
}
