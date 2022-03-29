/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that transforms core property declarations.
 */
object PropertyTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PropertyTransformerVisitor)
    }

    private object PropertyTransformerVisitor : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is CorePropertyDeclaration)
                referenceExpression.replace(reference.transform(referenceExpression))
        }
    }
}
