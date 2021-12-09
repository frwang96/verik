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

import io.verik.compiler.common.TextFile
import io.verik.compiler.message.GradleMessagePrinter
import io.verik.compiler.message.MessageCollector
import java.nio.file.Files

object VerikMain {

    fun run(config: VerikConfig, stageSequence: StageSequence) {
        MessageCollector.messageCollector = MessageCollector(config, GradleMessagePrinter(config))
        val projectContext = ProjectContext(config)
        readFiles(projectContext)
        stageSequence.process(projectContext)
        writeFiles(projectContext)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.sourceSetContexts = projectContext.config.sourceSetConfigs.map { sourceSetConfig ->
            val textFiles = sourceSetConfig.files.map {
                val lines = Files.readAllLines(it)
                TextFile(it, lines.joinToString(separator = "\n", postfix = "\n"))
            }
            SourceSetContext(sourceSetConfig.name, textFiles)
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        MessageCollector.messageCollector.flush()
        if (Files.exists(projectContext.config.buildDir)) {
            projectContext.config.buildDir.toFile().deleteRecursively()
        }
        val textFiles = projectContext.outputContext.getTextFiles()
        textFiles.forEach {
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }
}
