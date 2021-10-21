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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.message.Messages

object ElementSpecializer {

    fun <E : EElement> specialize(element: E, specializerContext: SpecializerContext): E {
        val copiedElement = when (element) {
            is EDeclaration -> DeclarationSpecializer.specializeDeclaration(element, specializerContext)
            is EExpression -> ExpressionSpecializer.specializeExpression(element, specializerContext)
            is EAnnotation -> specializeAnnotation(element)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to specialize element: $element")
                element
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun specializeAnnotation(annotation: EAnnotation): EAnnotation {
        return EAnnotation(
            annotation.location,
            annotation.name,
            annotation.qualifiedName,
            annotation.arguments
        )
    }
}