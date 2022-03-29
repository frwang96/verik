/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.evaluate.ExpressionEvaluator
import io.verik.compiler.target.common.Target

/**
 * Utility class for transforming expressions.
 */
object CoreTransformUtil {

    fun callExpressionDecrement(expression: EExpression): EExpression {
        val callExpression = ECallExpression(
            expression.location,
            Core.Kt.C_Int.toType(),
            Core.Kt.Int.F_minus_Int,
            expression,
            false,
            arrayListOf(ConstantBuilder.buildInt(expression, 1)),
            ArrayList()
        )
        return ExpressionEvaluator.evaluate(callExpression) ?: callExpression
    }

    fun callExpressionSigned(expression: EExpression): ECallExpression {
        return ECallExpression(
            expression.location,
            Core.Vk.C_Sbit.toType(expression.type.getWidthAsType(expression)),
            Target.F_signed,
            null,
            false,
            arrayListOf(expression),
            ArrayList()
        )
    }

    fun callExpressionUnsigned(expression: EExpression): ECallExpression {
        return ECallExpression(
            expression.location,
            Core.Vk.C_Ubit.toType(expression.type.getWidthAsType(expression)),
            Target.F_unsigned,
            null,
            false,
            arrayListOf(expression),
            ArrayList()
        )
    }
}
