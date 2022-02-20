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

package io.verik.compiler.serialize.general

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import java.nio.file.Path

object WrapperSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val packageWrapperTextFiles = ArrayList<TextFile>()
        projectContext.project.regularPackages().forEach {
            val packageWrapperTextFile = serializePackageWrapper(it, projectContext)
            if (packageWrapperTextFile != null) {
                packageWrapperTextFiles.add(packageWrapperTextFile)
            }
        }
        val projectWrapperTextFile = serializeProjectWrapper(projectContext, packageWrapperTextFiles)
        projectContext.outputContext.packageWrapperTextFiles = packageWrapperTextFiles
        projectContext.outputContext.projectWrapperTextFile = projectWrapperTextFile
    }

    private fun serializePackageWrapper(pkg: EPackage, projectContext: ProjectContext): TextFile? {
        if (pkg.files.all { it.isEmptySerialization() })
            return null

        val outputPath = pkg.outputPath.resolve("Pkg.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            outputPath,
            FileHeaderBuilder.CommentStyle.SLASH
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        if (!pkg.packageType.isRoot()) {
            builder.appendLine()
            builder.appendLine("package ${pkg.name};")
        }
        serializeInjectedProperties(pkg.injectedProperties, builder)
        serializeFiles(pkg.files, projectContext, builder)
        if (!pkg.packageType.isRoot()) {
            builder.appendLine()
            builder.appendLine("endpackage : ${pkg.name}")
        }
        return TextFile(outputPath, builder.toString())
    }

    private fun serializeProjectWrapper(
        projectContext: ProjectContext,
        packageWrapperTextFiles: List<TextFile>
    ): TextFile {
        val outputPath = projectContext.config.buildDir.resolve("top.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            outputPath,
            FileHeaderBuilder.CommentStyle.SLASH
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine()
        builder.appendLine("`timescale ${projectContext.config.timescale}")
        if (projectContext.config.labelLines) {
            builder.appendLine()
            builder.appendLine("`define _(n)")
        }
        projectContext.outputContext.targetPackageTextFile?.let { serializeInclude(it.path, projectContext, builder) }
        packageWrapperTextFiles.forEach { serializeInclude(it.path, projectContext, builder) }
        return TextFile(outputPath, builder.toString())
    }

    private fun serializeInjectedProperties(injectedProperties: List<EInjectedProperty>, builder: StringBuilder) {
        injectedProperties.forEach {
            val entries = it.injectedExpression.entries.filterIsInstance<LiteralStringEntry>()
            if (entries.isNotEmpty()) {
                builder.appendLine()
                entries.forEach { entry -> builder.append(entry.text) }
                builder.appendLine()
            }
        }
    }

    private fun serializeFiles(files: List<EFile>, projectContext: ProjectContext, builder: StringBuilder) {
        files.forEach { file ->
            file.declarations.forEach {
                if (it is ESvClass) {
                    builder.appendLine()
                    builder.appendLine("typedef class ${it.name};")
                }
            }
        }
        files.forEach {
            if (!it.isEmptySerialization()) {
                serializeInclude(it.outputPath, projectContext, builder)
            }
        }
    }

    private fun serializeInclude(path: Path, projectContext: ProjectContext, builder: StringBuilder) {
        val pathString = Platform.getStringFromPath(
            projectContext.config.buildDir.relativize(path)
        )
        builder.appendLine()
        builder.appendLine("`include \"$pathString\"")
    }
}
