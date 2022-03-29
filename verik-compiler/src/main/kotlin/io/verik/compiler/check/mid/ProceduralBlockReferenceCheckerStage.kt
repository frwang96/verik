/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks that there are no references to procedural blocks.
 */
object ProceduralBlockReferenceCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ProceduralBlockReferenceCheckerVisitor)
    }

    private object ProceduralBlockReferenceCheckerVisitor : TreeVisitor() {

        private val proceduralBlockAnnotationEntries = listOf(
            AnnotationEntries.COM,
            AnnotationEntries.SEQ,
            AnnotationEntries.RUN
        )

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is EKtFunction) {
                if (proceduralBlockAnnotationEntries.any { reference.hasAnnotationEntry(it) }) {
                    Messages.PROCEDURAL_BLOCK_ILLEGAL_REFERENCE.on(callExpression, reference.name)
                }
            }
        }
    }
}
