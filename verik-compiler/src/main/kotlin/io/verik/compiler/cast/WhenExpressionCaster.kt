/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

object WhenExpressionCaster {

    fun castWhenExpression(expression: KtWhenExpression, castContext: CastContext): EWhenExpression {
        val location = expression.location()
        val endLocation = expression.closeBrace!!.location()
        val type = castContext.castType(expression)
        val subject = expression.subjectExpression?.let { castContext.casterVisitor.getExpression(it) }
        val entries = expression.entries.map { castWhenEntry(it, castContext) }
        return EWhenExpression(location, endLocation, type, subject, entries)
    }

    private fun castWhenEntry(whenEntry: KtWhenEntry, castContext: CastContext): WhenEntry {
        val conditions = whenEntry.conditions.mapNotNull { castWhenCondition(it, castContext) }
        val body = castContext.casterVisitor.getExpression(whenEntry.expression!!)
        return WhenEntry(ArrayList(conditions), EKtBlockExpression.wrap(body))
    }

    private fun castWhenCondition(whenCondition: KtWhenCondition, castContext: CastContext): EExpression {
        return if (whenCondition is KtWhenConditionWithExpression) {
            castContext.casterVisitor.getExpression(whenCondition.expression!!)
        } else {
            Messages.INTERNAL_ERROR.on(whenCondition, "When condition type not supported")
        }
    }
}
