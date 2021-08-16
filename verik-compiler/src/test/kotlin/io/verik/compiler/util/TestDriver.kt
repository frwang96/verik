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
import io.verik.compiler.check.normalize.NormalizationChecker
import io.verik.compiler.check.post.ProjectPostChecker
import io.verik.compiler.check.pre.ProjectPreChecker
import io.verik.compiler.interpret.ProjectInterpreter
import io.verik.compiler.main.KotlinCompiler
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.resolve.ProjectResolver
import io.verik.compiler.serialize.ProjectSerializer
import io.verik.compiler.specialize.ProjectSpecializer
import io.verik.compiler.transform.mid.ProjectMidTransformer
import io.verik.compiler.transform.post.ProjectPostTransformer
import io.verik.compiler.transform.pre.ProjectPreTransformer
import io.verik.plugin.Config
import org.intellij.lang.annotations.Language
import java.nio.file.Paths

object TestDriver {

    fun compile(@Language("kotlin") content: String): ProjectContext {
        val contentWithPackageHeader = """
            package verik
            import io.verik.core.*
            $content
        """.trimIndent()
        val textFile = TextFile(Paths.get("/src/main/kotlin/verik/Test.kt"), contentWithPackageHeader)
        val config = Config(
            "unspecified",
            "",
            "verik",
            Paths.get("/"),
            Paths.get("/build/verik"),
            listOf(textFile.path),
            "*",
            verbose = false,
            debug = true,
            suppressCompileWarnings = true,
            labelLines = false,
            alignLength = 0,
            120,
            4
        )
        val projectContext = ProjectContext(config)
        projectContext.inputTextFiles = listOf(textFile)
        KotlinCompiler().pass(projectContext)
        return projectContext
    }

    fun cast(@Language("kotlin") content: String): ProjectContext {
        val projectContext = compile(content)
        ProjectCaster.pass(projectContext)
        return projectContext
    }

    fun preCheck(@Language("kotlin") content: String): ProjectContext {
        val projectContext = cast(content)
        ProjectPreChecker.pass(projectContext)
        return projectContext
    }

    fun preTransform(@Language("kotlin") content: String): ProjectContext {
        val projectContext = preCheck(content)
        ProjectPreTransformer.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun resolve(@Language("kotlin") content: String): ProjectContext {
        val projectContext = preTransform(content)
        ProjectResolver.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun specialize(@Language("kotlin") content: String): ProjectContext {
        val projectContext = resolve(content)
        ProjectSpecializer.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun interpret(@Language("kotlin") content: String): ProjectContext {
        val projectContext = specialize(content)
        ProjectInterpreter.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun midTransform(@Language("kotlin") content: String): ProjectContext {
        val projectContext = interpret(content)
        ProjectMidTransformer.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun postTransform(@Language("kotlin") content: String): ProjectContext {
        val projectContext = midTransform(content)
        ProjectPostTransformer.pass(projectContext)
        NormalizationChecker.pass(projectContext)
        return projectContext
    }

    fun postCheck(@Language("kotlin") content: String): ProjectContext {
        val projectContext = postTransform(content)
        ProjectPostChecker.pass(projectContext)
        return projectContext
    }

    fun serialize(@Language("kotlin") content: String): ProjectContext {
        val projectContext = postCheck(content)
        ProjectSerializer.pass(projectContext)
        return projectContext
    }
}