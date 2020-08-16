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

package verik.core.vk

import verik.core.kt.KtAnnotationProperty
import verik.core.ktx.KtxDeclaration
import verik.core.ktx.KtxDeclarationBaseProperty
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

data class VkBaseProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val expression: VkExpression
): VkProperty {

    companion object {

        fun isBaseProperty(declaration: KtxDeclaration): Boolean {
            return declaration is KtxDeclarationBaseProperty && declaration.annotations.none {
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

        operator fun invoke(declaration: KtxDeclaration): VkBaseProperty {
            val baseProperty = declaration.let {
                if (it is KtxDeclarationBaseProperty) it
                else throw LineException("base property declaration expected", it)
            }
            if (baseProperty.annotations.isNotEmpty()) {
                throw LineException("property annotations are not supported here", baseProperty)
            }

            val expression = VkExpression(baseProperty.expression)
            return VkBaseProperty(
                    baseProperty.line,
                    baseProperty.identifier,
                    baseProperty.symbol,
                    expression.type,
                    expression
            )
        }
    }
}