/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor

/**
 * Utility class for unrolling expressions.
 */
object UnrollUtil {

    fun substituteValueParameter(
        expression: EExpression,
        valueParameter: EAbstractValueParameter,
        substitutedExpression: EExpression
    ) {
        val valueParameterSubstitutorVisitor = ValueParameterSubstitutorVisitor(valueParameter, substitutedExpression)
        expression.accept(valueParameterSubstitutorVisitor)
    }

    private class ValueParameterSubstitutorVisitor(
        private val valueParameter: EAbstractValueParameter,
        private val expression: EExpression
    ) : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            if (referenceExpression.reference == valueParameter) {
                val copiedExpression = ExpressionCopier.deepCopy(expression, referenceExpression.location)
                referenceExpression.replace(copiedExpression)
            }
        }
    }
}
