/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.kotlin

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Stage that runs the Kotlin compiler parser.
 */
object KotlinCompilerParserStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val psiFileFactory = KtPsiFactory(projectContext.kotlinCoreEnvironment.project, false)
        projectContext.sourceSetContexts.forEach { sourceSetContext ->
            sourceSetContext.ktFiles = sourceSetContext.textFiles.map {
                psiFileFactory.createPhysicalFile(it.path.toString(), it.content)
            }
        }
    }
}
