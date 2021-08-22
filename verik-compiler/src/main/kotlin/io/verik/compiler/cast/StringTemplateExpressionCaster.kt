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

import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.location
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*

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
                val expression = castContext.casterVisitor.getExpression(entry.expression!!)
                ExpressionStringEntry(expression)
            }
            else -> {
                m.error("Unrecognized string template entry: ${entry::class.simpleName}", entry)
                LiteralStringEntry("")
            }
        }
    }
}