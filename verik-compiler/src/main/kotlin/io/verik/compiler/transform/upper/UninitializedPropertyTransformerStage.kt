/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object UninitializedPropertyTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UninitializedPropertyTransformerVisitor)
    }

    private object UninitializedPropertyTransformerVisitor : TreeVisitor() {

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (initializer is ECallExpression && initializer.reference == Core.Vk.F_nc) {
                property.initializer = null
            }
        }
    }
}
