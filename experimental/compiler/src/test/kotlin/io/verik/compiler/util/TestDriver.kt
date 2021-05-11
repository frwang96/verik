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

import io.verik.compiler.cast.ProjectCaster
import io.verik.compiler.check.ProjectChecker
import io.verik.compiler.main.KotlinCompiler
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.plugin.Config
import org.intellij.lang.annotations.Language
import java.nio.file.Paths

object TestDriver {

    fun compile(@Language("kotlin") content: String): ProjectContext {
        val contentWithPackageHeader = """
            package verik
            $content
        """.trimIndent()
        val textFile = TextFile(Paths.get("/src/main/kotlin/verik/Test.kt"), contentWithPackageHeader)
        val config = Config(
            "",
            "verik",
            Paths.get("/"),
            Paths.get("/build/verik"),
            listOf(textFile.path),
            "*",
            verbose = false,
            printStackTrace = false,
            labelLines = true,
            120,
            4
        )
        val projectContext = ProjectContext(config)
        projectContext.inputTextFiles = listOf(textFile)
        KotlinCompiler().compile(projectContext)
        return projectContext
    }

    fun cast(@Language("kotlin") content: String): ProjectContext {
        val projectContext = compile(content)
        ProjectCaster.cast(projectContext)
        return projectContext
    }

    fun check(@Language("kotlin") content: String): ProjectContext {
        val projectContext = cast(content)
        ProjectChecker.check(projectContext)
        return projectContext
    }
}