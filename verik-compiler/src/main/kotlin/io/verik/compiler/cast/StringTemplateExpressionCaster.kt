/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

object StringTemplateExpressionCaster {

    fun castStringTemplateExpression(
        expression: KtStringTemplateExpression,
        castContext: CastContext
    ): EStringTemplateExpression {
        val location = expression.location()
        val entries = expression.entries.mapNotNull {
            castStringTemplateEntry(it, castContext)
        }
        return EStringTemplateExpression(location, entries)
    }

    private fun castStringTemplateEntry(entry: KtStringTemplateEntry, castContext: CastContext): StringEntry {
        return when (entry) {
            is KtLiteralStringTemplateEntry -> LiteralStringEntry(entry.text)
            is KtEscapeStringTemplateEntry -> LiteralStringEntry(entry.unescapedValue)
            is KtStringTemplateEntryWithExpression -> {
                val expression = castContext.castExpression(entry.expression!!)
                ExpressionStringEntry(expression)
            }
            else -> {
                Messages.INTERNAL_ERROR.on(entry, "Unrecognized string template entry: ${entry::class.simpleName}")
            }
        }
    }
}
