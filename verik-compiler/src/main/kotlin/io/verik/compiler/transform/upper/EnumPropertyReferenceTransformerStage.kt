/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object EnumPropertyReferenceTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(EnumPropertyReferenceTransformerVisitor)
    }

    private object EnumPropertyReferenceTransformerVisitor : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EProperty) {
                val parent = reference.parent
                if (parent is EEnum) {
                    val receiver = referenceExpression.receiver!!
                    receiver.type = referenceExpression.type
                    referenceExpression.replace(receiver)
                }
            }
        }
    }
}
