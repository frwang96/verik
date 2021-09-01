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
import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object AnnotationCaster {

    fun castAnnotationEntry(annotationEntry: KtAnnotationEntry, castContext: CastContext): EAnnotation {
        val location = annotationEntry.location()
        val descriptor = castContext.sliceAnnotation[annotationEntry]!!
        val name = descriptor.fqName!!.shortName().asString()
        val qualifiedName = descriptor.fqName!!.asString()
        val arguments = annotationEntry.valueArguments.map {
            val expression = castContext.casterVisitor.getExpression(it.getArgumentExpression()!!)
            castAnnotationArgument(expression)
        }
        return EAnnotation(location, name, qualifiedName, arguments)
    }

    private fun castAnnotationArgument(expression: EExpression): String {
        return if (expression is EStringTemplateExpression && expression.entries.size == 1) {
            val stringEntry = expression.entries[0]
            if (stringEntry is LiteralStringEntry) {
                stringEntry.text
            } else {
                Messages.ANNOTATION_ARGUMENT_NOT_LITERAL.on(expression)
                ""
            }
        } else {
            Messages.ANNOTATION_ARGUMENT_NOT_LITERAL.on(expression)
            ""
        }
    }
}
