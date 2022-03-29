/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.source

import io.verik.importer.ast.element.declaration.EKtPackage
import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that serializes source text files.
 */
object SourceSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val sourceTextFiles = ArrayList<TextFile>()
        val packages = projectContext.project.declarations.filterIsInstance<EKtPackage>()
        packages.forEach { pkg ->
            pkg.files.forEach { file ->
                val serializeContext = SerializeContext(projectContext, pkg.name, file.outputPath)
                file.declarations.forEach { serializeContext.serialize(it) }
                sourceTextFiles.add(serializeContext.getTextFile())
            }
        }
        projectContext.outputContext.sourceTextFiles = sourceTextFiles
    }
}
