/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression

object UbitConstantEvaluator {

    fun plusUbit(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftBitConstant = ConstantNormalizer.parseBitConstant(left)
        val rightBitConstant = ConstantNormalizer.parseBitConstant(right)
        return ConstantBuilder.buildBitConstant(original, leftBitConstant.plus(rightBitConstant))
    }

    fun minusUbit(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftBitConstant = ConstantNormalizer.parseBitConstant(left)
        val rightBitConstant = ConstantNormalizer.parseBitConstant(right)
        return ConstantBuilder.buildBitConstant(original, leftBitConstant.minus(rightBitConstant))
    }
}
