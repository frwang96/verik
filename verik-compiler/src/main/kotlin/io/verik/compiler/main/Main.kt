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

import io.verik.compiler.cast.ProjectCaster
import io.verik.compiler.check.post.ProjectPostChecker
import io.verik.compiler.check.pre.ProjectPreChecker
import io.verik.compiler.interpret.ProjectInterpreter
import io.verik.compiler.resolve.ProjectResolver
import io.verik.compiler.serialize.ProjectSerializer
import io.verik.compiler.specialize.ProjectSpecializer
import io.verik.compiler.transform.mid.ProjectMidTransformer
import io.verik.compiler.transform.post.ProjectPostTransformer
import io.verik.compiler.transform.pre.ProjectPreTransformer
import io.verik.plugin.Config
import java.nio.file.Files

lateinit var m: MessageCollector

object Main {

    fun run(config: Config) {
        m = GradleMessageCollector(config)
        val projectContext = ProjectContext(config)

        readFiles(projectContext)
        KotlinCompiler().pass(projectContext)
        ProjectCaster.pass(projectContext)
        ProjectPreChecker.pass(projectContext)
        ProjectPreTransformer.pass(projectContext)
        ProjectResolver.pass(projectContext)
        ProjectSpecializer.pass(projectContext)
        ProjectInterpreter.pass(projectContext)
        ProjectMidTransformer.pass(projectContext)
        ProjectPostTransformer.pass(projectContext)
        ProjectPostChecker.pass(projectContext)
        ProjectSerializer.pass(projectContext)
        writeFiles(projectContext)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.inputTextFiles = projectContext.config.projectFiles.map {
            m.info("Read file: ${projectContext.config.projectDir.relativize(it)}")
            TextFile(it, Files.readString(it))
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        m.flush()
        if (Files.exists(projectContext.config.buildDir)) {
            Files.walk(projectContext.config.buildDir)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
        }
        val outputTextFiles = projectContext.outputTextFiles.sortedBy { it.path }
        outputTextFiles.forEach {
            m.info("Write file: ${projectContext.config.projectDir.relativize(it.path)}")
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }
}