/*
 * SPDX-License-Identifier: Apache-2.0
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

/**
 * Stage that serializes the wrapper file. The wrapper file has include directives to all of the other generated source
 * files.
 */
object WrapperSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("out.sv")
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
        projectContext.project.regularNonRootPackages.forEach { serializePackage(it, projectContext, builder) }
        val rootPackage = projectContext.project.regularRootPackage
        serializeInjectedProperties(rootPackage.injectedProperties, builder)
        serializeFiles(rootPackage.files, projectContext, builder)
        projectContext.outputContext.wrapperTextFile = TextFile(outputPath, builder.toString())
    }

    private fun serializePackage(pkg: EPackage, projectContext: ProjectContext, builder: StringBuilder) {
        if (pkg.files.all { it.isEmptySerialization() }) return
        builder.appendLine()
        builder.appendLine("package ${pkg.name};")
        serializeInjectedProperties(pkg.injectedProperties, builder)
        serializeFiles(pkg.files, projectContext, builder)
        builder.appendLine()
        builder.appendLine("endpackage : ${pkg.name}")
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
