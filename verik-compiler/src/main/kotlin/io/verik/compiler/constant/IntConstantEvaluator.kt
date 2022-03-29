/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression

/**
 * Evaluator for operators on integer constants.
 */
object IntConstantEvaluator {

    fun plusInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt + rightInt)
    }

    fun minusInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt - rightInt)
    }

    fun timesInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt * rightInt)
    }
}
