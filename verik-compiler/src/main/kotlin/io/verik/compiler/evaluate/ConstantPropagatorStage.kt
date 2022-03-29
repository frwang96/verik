/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that propagates properties that are constant expressions. Most constant propagation happens in
 * [ConstantPropagatorSubstage] during specialization. This catches the remaining constant expressions that need to be
 * propagated.
 */
object ConstantPropagatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ConstantPropagatorVisitor)
    }

    private object ConstantPropagatorVisitor : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EProperty && !reference.isMutable) {
                val initializer = reference.initializer
                if (initializer != null) {
                    val copiedInitializer = ExpressionCopier.deepCopy(initializer, referenceExpression.location)
                    val expandedInitializer = ConstantPropagator.expand(copiedInitializer)
                    if (ConstantPropagator.isConstant(expandedInitializer)) {
                        referenceExpression.replace(expandedInitializer)
                    }
                }
            }
        }
    }
}
