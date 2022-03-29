/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.specialize.SpecializerSubstage
import io.verik.compiler.specialize.TypeParameterBinding

/**
 * Specializer substage that evaluates expressions.
 */
object ExpressionEvaluatorSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        declaration.accept(ExpressionEvaluatorVisitor)
    }

    private object ExpressionEvaluatorVisitor : TreeVisitor() {

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            val evaluatedExpression = ExpressionEvaluator.evaluate(expression)
            if (evaluatedExpression != null)
                expression.replace(evaluatedExpression)
        }
    }
}
