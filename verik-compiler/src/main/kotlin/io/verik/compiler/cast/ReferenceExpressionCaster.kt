/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Caster that checks for reference expressions that are smart casts.
 */
object ReferenceExpressionCaster {

    fun checkSmartCast(
        expression: KtExpression,
        referenceExpression: EReferenceExpression,
        castContext: CastContext
    ) {
        val explicitSmartCasts = castContext.sliceSmartCast[expression]
        if (explicitSmartCasts != null) {
            explicitSmartCasts.type(null)?.let { referenceExpression.type = castContext.castType(it, expression) }
            castContext.smartCastExpressions.add(referenceExpression)
        }
    }
}
