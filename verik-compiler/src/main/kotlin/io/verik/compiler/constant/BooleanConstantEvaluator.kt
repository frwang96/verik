/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression

/**
 * Evaluator for operators on boolean constants.
 */
object BooleanConstantEvaluator {

    fun binaryAndBoolean(original: EExpression, left: EExpression, right: EExpression): EExpression? {
        val leftKind = ConstantNormalizer.parseBooleanOrNull(left)
        val rightKind = ConstantNormalizer.parseBooleanOrNull(right)
        return when {
            leftKind == BooleanConstantKind.FALSE || rightKind == BooleanConstantKind.FALSE ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.FALSE)
            leftKind == BooleanConstantKind.TRUE -> right
            rightKind == BooleanConstantKind.TRUE -> left
            leftKind != null && leftKind.isUnknownOrFloating() ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.UNKNOWN)
            rightKind != null && rightKind.isUnknownOrFloating() ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.UNKNOWN)
            else -> null
        }
    }

    fun binaryOrBoolean(original: EExpression, left: EExpression, right: EExpression): EExpression? {
        val leftKind = ConstantNormalizer.parseBooleanOrNull(left)
        val rightKind = ConstantNormalizer.parseBooleanOrNull(right)
        return when {
            leftKind == BooleanConstantKind.TRUE || rightKind == BooleanConstantKind.TRUE ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.TRUE)
            leftKind == BooleanConstantKind.FALSE -> right
            rightKind == BooleanConstantKind.FALSE -> left
            leftKind != null && leftKind.isUnknownOrFloating() ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.UNKNOWN)
            rightKind != null && rightKind.isUnknownOrFloating() ->
                ConstantBuilder.buildBoolean(original, BooleanConstantKind.UNKNOWN)
            else -> null
        }
    }

    fun not(original: EExpression, expression: EConstantExpression): EConstantExpression {
        return when (ConstantNormalizer.parseBoolean(expression)) {
            BooleanConstantKind.TRUE -> ConstantBuilder.buildBoolean(original, BooleanConstantKind.FALSE)
            BooleanConstantKind.FALSE -> ConstantBuilder.buildBoolean(original, BooleanConstantKind.TRUE)
            else -> ConstantBuilder.buildBoolean(original, BooleanConstantKind.UNKNOWN)
        }
    }
}
