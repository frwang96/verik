/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.test

import io.verik.importer.common.TextFile
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.OutputContext
import io.verik.importer.main.Platform
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.VerikImporterConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo

internal class RegressionTest {

    private val DOCSTRING_REGEX = Regex("(\\s*/\\*\\*.*|\\s*\\*.*)")

    @TestFactory
    fun regression(): List<DynamicTest> {
        val tests = ArrayList<DynamicTest>()
        val regressionDir = Paths.get("regression")
        val systemVerilogFiles = regressionDir.listDirectoryEntries().filter { it.extension == "sv" }
        systemVerilogFiles.forEach {
            val match = Regex("regression/\\d+-([\\w-]+)\\.\\w+").matchEntire(Platform.getStringFromPath(it))
            if (match != null) {
                val name = match.destructured.component1().replace("-", " ")
                val test = DynamicTest.dynamicTest(name) {
                    runTest(it)
                }
                tests.add(test)
            }
        }
        return tests
    }

    private fun runTest(systemVerilogFile: Path) {
        val kotlinFile = systemVerilogFile.parent.resolve("${systemVerilogFile.nameWithoutExtension}.kt")
        assert(systemVerilogFile.exists())
        assert(kotlinFile.exists())
        val systemVerilogTextFiles = parseTextFiles(Files.readAllLines(systemVerilogFile))
        val kotlinTextFiles = parseTextFiles(Files.readAllLines(kotlinFile))
        assert(systemVerilogTextFiles.isNotEmpty()) { "No SystemVerilog text files found" }
        assert(kotlinTextFiles.isNotEmpty()) { "No Kotlin text files found" }
        val outputContext = getOutputContext(systemVerilogTextFiles)
        val expectedString = getExpectedString(kotlinTextFiles)
        val actualString = getActualString(outputContext)
        assertEquals(expectedString, actualString)
    }

    private fun parseTextFiles(fileLines: List<String>): List<TextFile> {
        val pathRegex = Regex("//\\s+([\\w./]+)\\s+/+")
        var currentPath: Path? = null
        val textFiles = ArrayList<TextFile>()
        val builder = StringBuilder()
        fileLines.forEach {
            val match = pathRegex.matchEntire(it)
            if (match != null) {
                if (currentPath != null) {
                    textFiles.add(TextFile(currentPath!!, builder.toString()))
                    builder.clear()
                }
                currentPath = Paths.get(match.destructured.component1())
            } else {
                if (currentPath != null) {
                    builder.appendLine(it)
                }
            }
        }
        if (currentPath != null) {
            textFiles.add(TextFile(currentPath!!, builder.toString()))
        }
        return textFiles
    }

    private fun getOutputContext(systemVerilogTextFiles: List<TextFile>): OutputContext {
        val projectContext = getProjectContext(systemVerilogTextFiles)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processAll(projectContext)
        return projectContext.outputContext
    }

    private fun getProjectContext(systemVerilogTextFiles: List<TextFile>): ProjectContext {
        val importedFiles = systemVerilogTextFiles.map { it.path }
        val config = VerikImporterConfig(
            toolchain = "verik",
            timestamp = "",
            projectName = "test",
            buildDir = Paths.get(""),
            importedFiles = importedFiles,
            includeDirs = listOf(),
            enablePreprocessorOutput = false,
            suppressedWarnings = listOf(),
            promotedWarnings = listOf(),
            maxErrorCount = 0,
            debug = true
        )
        val projectContext = ProjectContext(config)
        val inputFileContexts = systemVerilogTextFiles.map { InputFileContext(it) }
        projectContext.inputFileContexts = inputFileContexts
        return projectContext
    }

    private fun getExpectedString(kotlinTextFiles: List<TextFile>): String {
        val builder = StringBuilder()
        kotlinTextFiles.forEach {
            val pathLine = "// ${Platform.getStringFromPath(it.path)} ".padEnd(120, '/')
            builder.appendLine(pathLine)
            builder.appendLine()
            builder.append(it.content.trim())
            builder.appendLine()
            builder.appendLine()
        }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    private fun getActualString(outputContext: OutputContext): String {
        val builder = StringBuilder()
        val textFiles = outputContext.getTextFiles().filter { it.path.extension == "kt" }
        textFiles.forEach { textFile ->
            val path = textFile.path.relativeTo(Paths.get("src/imported"))
            val pathLine = "// ${Platform.getStringFromPath(path)} ".padEnd(120, '/')
            builder.appendLine(pathLine)
            var isHeader = true
            for (line in textFile.content.lines()) {
                if (line.startsWith("package")) {
                    builder.appendLine()
                    isHeader = false
                }
                if (isHeader)
                    continue
                if (!line.matches(DOCSTRING_REGEX)) {
                    builder.appendLine(line)
                }
            }
        }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            BaseTest.setup()
        }
    }
}
