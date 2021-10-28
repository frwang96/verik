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

package io.verik.compiler.main

import io.verik.compiler.message.GradleMessagePrinter
import io.verik.compiler.message.MessageCollector
import java.nio.file.Files

object Main {

    fun run(config: Config) {
        MessageCollector.messageCollector = MessageCollector(config, GradleMessagePrinter(config.debug))
        val projectContext = ProjectContext(config)
        val stageSequence = StageSequencer.getStageSequence()

        readFiles(projectContext)
        stageSequence.process(projectContext)
        writeFiles(projectContext)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.inputTextFiles = projectContext.config.projectFiles.map {
            val lines = Files.readAllLines(it)
            TextFile(it, lines.joinToString(separator = "\n", postfix = "\n"))
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        MessageCollector.messageCollector.flush()
        if (Files.exists(projectContext.config.buildDir)) {
            Files.walk(projectContext.config.buildDir)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
        }
        val textFiles = projectContext.outputContext.getTextFiles()
        textFiles.forEach {
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }
}
