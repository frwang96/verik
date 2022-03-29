/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Caster that builds [when expressions][EWhenExpression].
 */
object WhenExpressionCaster {

    fun castWhenExpression(expression: KtWhenExpression, castContext: CastContext): EWhenExpression {
        val location = expression.location()
        val endLocation = expression.closeBrace!!.location()
        val type = castContext.castType(expression)
        val subject = expression.subjectExpression?.let { castContext.castExpression(it) }
        val entries = expression.entries.map { castWhenEntry(it, castContext) }
        return EWhenExpression(location, endLocation, type, subject, entries)
    }

    private fun castWhenEntry(whenEntry: KtWhenEntry, castContext: CastContext): WhenEntry {
        val conditions = whenEntry.conditions.mapNotNull { castWhenCondition(it, castContext) }
        val body = castContext.castExpression(whenEntry.expression!!)
        return WhenEntry(ArrayList(conditions), EBlockExpression.wrap(body))
    }

    private fun castWhenCondition(whenCondition: KtWhenCondition, castContext: CastContext): EExpression {
        return if (whenCondition is KtWhenConditionWithExpression) {
            castContext.castExpression(whenCondition.expression!!)
        } else {
            Messages.INTERNAL_ERROR.on(whenCondition, "When condition type not supported")
        }
    }
}
