/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.sv.EStructLiteralExpression
import io.verik.compiler.ast.property.StructLiteralEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object StructLiteralTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(StructLiteralTransformerVisitor)
    }

    private object StructLiteralTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is EStruct) {
                val entries = callExpression.valueArguments
                    .zip(reference.properties)
                    .map { (valueArgument, property) ->
                        StructLiteralEntry(property, valueArgument)
                    }
                val structLiteralExpression = EStructLiteralExpression(
                    callExpression.location,
                    callExpression.type.copy(),
                    entries
                )
                callExpression.replace(structLiteralExpression)
            }
        }
    }
}
