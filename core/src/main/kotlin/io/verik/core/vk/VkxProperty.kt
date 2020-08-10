/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.vk

import io.verik.core.LineException
import io.verik.core.kt.KtAnnotationProperty
import io.verik.core.kt.KtDeclaration
import io.verik.core.kt.KtDeclarationProperty
import io.verik.core.symbol.Symbol

data class VkxProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val expression: VkxExpression
): VkxDeclaration {

    companion object {

        fun isProperty(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationProperty && declaration.annotations.none {
                it in listOf(
                        KtAnnotationProperty.INPUT,
                        KtAnnotationProperty.OUTPUT,
                        KtAnnotationProperty.INOUT,
                        KtAnnotationProperty.INTERF,
                        KtAnnotationProperty.MODPORT,
                        KtAnnotationProperty.COMP
                )
            }
        }

        operator fun invoke(property: KtDeclarationProperty): VkxProperty {
            if (property.annotations.isNotEmpty()) {
                throw LineException("property annotations are not supported here", property)
            }
            return VkxProperty(
                    property.line,
                    property.identifier,
                    property.symbol,
                    VkxExpression(property.expression)
            )
        }
    }
}