/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.test

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.OutputContext
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.SourceSetConfig
import io.verik.compiler.main.SourceSetContext
import io.verik.compiler.main.StageSequencer
import io.verik.compiler.main.VerikConfig
import io.verik.compiler.message.MessageCollector
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

internal class RegressionTest {

    @TestFactory
    fun regression(): List<DynamicTest> {
        val tests = ArrayList<DynamicTest>()
        val regressionDir = Paths.get("regression")
        val kotlinFiles = regressionDir.listDirectoryEntries().filter { it.extension == "kt" }
        kotlinFiles.forEach {
            val match = Regex("regression/\\d+-(\\w+)\\.\\w+").matchEntire(Platform.getStringFromPath(it))
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

    private fun runTest(kotlinFile: Path) {
        val systemVerilogFile = kotlinFile.parent.resolve("${kotlinFile.nameWithoutExtension}.sv")
        assert(kotlinFile.exists())
        assert(systemVerilogFile.exists())
        val kotlinTextFiles = parseTextFiles(Files.readAllLines(kotlinFile))
        val systemVerilogTextFiles = parseTextFiles(Files.readAllLines(systemVerilogFile))
        val outputContext = getOutputContext(kotlinTextFiles)
        val expectedString = getExpectedString(systemVerilogTextFiles)
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

    private fun getOutputContext(kotlinTextFiles: List<TextFile>): OutputContext {
        val projectContext = getProjectContext(kotlinTextFiles)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processAll(projectContext)
        return projectContext.outputContext
    }

    private fun getProjectContext(kotlinTextFiles: List<TextFile>): ProjectContext {
        val sourceSetConfig = SourceSetConfig("test", kotlinTextFiles.map { it.path })
        val baseConfig = getBaseConfig()
        val config = VerikConfig(
            toolchain = baseConfig.toolchain,
            timestamp = baseConfig.timestamp,
            projectName = baseConfig.projectName,
            buildDir = baseConfig.buildDir,
            sourceSetConfigs = listOf(sourceSetConfig),
            timescale = baseConfig.timescale,
            entryPoints = baseConfig.entryPoints,
            enableDeadCodeElimination = baseConfig.enableDeadCodeElimination,
            labelLines = baseConfig.labelLines,
            enableLineDirective = baseConfig.enableLineDirective,
            indentLength = baseConfig.indentLength,
            wrapLength = baseConfig.wrapLength,
            suppressedWarnings = baseConfig.suppressedWarnings,
            promotedWarnings = baseConfig.promotedWarnings,
            maxErrorCount = baseConfig.maxErrorCount,
            debug = baseConfig.debug
        )
        val projectContext = ProjectContext(config)
        val sourceSetContext = SourceSetContext("test", kotlinTextFiles)
        projectContext.sourceSetContexts = listOf(sourceSetContext)
        return projectContext
    }

    private fun getExpectedString(systemVerilogTextFiles: List<TextFile>): String {
        val builder = StringBuilder()
        systemVerilogTextFiles.forEach {
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
        val textFiles = outputContext.getTextFiles().filter { it.path.extension in listOf("sv", "svh") }
        textFiles.forEach { textFile ->
            val pathLine = "// ${Platform.getStringFromPath(textFile.path)} ".padEnd(120, '/')
            builder.appendLine(pathLine)
            var isHeader = true
            for (line in textFile.content.lines()) {
                if (isHeader && line.startsWith("//"))
                    continue
                isHeader = false
                builder.appendLine(line)
            }
        }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getBaseConfig(), TestMessagePrinter())
        }

        fun getBaseConfig(): VerikConfig {
            return VerikConfig(
                toolchain = "verik",
                timestamp = "",
                projectName = "test",
                buildDir = Paths.get(""),
                sourceSetConfigs = listOf(),
                timescale = "1ns / 1ns",
                entryPoints = listOf(),
                enableDeadCodeElimination = true,
                labelLines = false,
                enableLineDirective = false,
                indentLength = 4,
                wrapLength = 80,
                suppressedWarnings = listOf(),
                promotedWarnings = listOf(),
                maxErrorCount = 0,
                debug = true
            )
        }
    }
}
