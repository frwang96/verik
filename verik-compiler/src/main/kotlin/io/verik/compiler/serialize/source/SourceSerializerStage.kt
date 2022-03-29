/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that serializes source text files.
 */
object SourceSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val nonRootPackageTextFiles = ArrayList<TextFile>()
        projectContext.project.regularNonRootPackages.forEach { regularPackage ->
            regularPackage.files.forEach {
                if (!it.isEmptySerialization()) {
                    nonRootPackageTextFiles.add(serialize(projectContext, it))
                }
            }
        }
        projectContext.outputContext.nonRootPackageTextFiles = nonRootPackageTextFiles

        val rootPackageTextFiles = ArrayList<TextFile>()
        projectContext.project.regularRootPackage.files.forEach {
            if (!it.isEmptySerialization()) {
                rootPackageTextFiles.add(serialize(projectContext, it))
            }
        }
        projectContext.outputContext.rootPackageTextFiles = rootPackageTextFiles
    }

    private fun serialize(projectContext: ProjectContext, file: EFile): TextFile {
        val serializeContext = SerializeContext(file)
        file.declarations.forEach {
            serializeContext.serializeAsDeclaration(it)
        }
        val sourceActionLines = serializeContext.getSourceActionLines()
        val sourceBuilder = SourceBuilder(projectContext, file)
        sourceBuilder.build(sourceActionLines)
        return sourceBuilder.toTextFile()
    }
}
