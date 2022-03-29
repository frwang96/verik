/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.constant.BooleanConstantEvaluator
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration

/**
 * Core declarations from Boolean.
 */
object CoreKtBoolean : CoreScope(Core.Kt.C_Boolean) {

    val F_not = object : UnaryCoreFunctionDeclaration(parent, "not", "fun not()", SvUnaryOperatorKind.LOGICAL_NEG) {

        override fun evaluate(callExpression: ECallExpression): EConstantExpression? {
            val expression = callExpression.receiver!!
            return if (expression is EConstantExpression) {
                BooleanConstantEvaluator.not(callExpression, expression)
            } else null
        }
    }

    val F_and_Boolean = BinaryCoreFunctionDeclaration(parent, "and", "fun and(Boolean)", SvBinaryOperatorKind.ANDAND)

    val F_or_Boolean = BinaryCoreFunctionDeclaration(parent, "or", "fun or(Boolean)", SvBinaryOperatorKind.OROR)

    val F_xor_Boolean = BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Boolean)", SvBinaryOperatorKind.XOR)
}
