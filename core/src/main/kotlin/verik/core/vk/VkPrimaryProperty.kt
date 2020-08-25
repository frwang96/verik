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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtAnnotationProperty
import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationPrimaryProperty

data class VkPrimaryProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val expression: VkExpression
): VkProperty {

    companion object {

        fun isPrimaryProperty(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationPrimaryProperty && declaration.annotations.none {
                it in listOf(
                        KtAnnotationProperty.INPUT,
                        KtAnnotationProperty.OUTPUT,
                        KtAnnotationProperty.INOUT,
                        KtAnnotationProperty.BUS,
                        KtAnnotationProperty.BUSPORT,
                        KtAnnotationProperty.MAKE
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkPrimaryProperty {
            val baseProperty = declaration.let {
                if (it is KtDeclarationPrimaryProperty) it
                else throw LineException("primary property declaration expected", it)
            }
            if (baseProperty.annotations.isNotEmpty()) {
                throw LineException("property annotations are not supported here", baseProperty)
            }

            val expression = VkExpression(baseProperty.expression)
            return VkPrimaryProperty(
                    baseProperty.line,
                    baseProperty.identifier,
                    baseProperty.symbol,
                    expression.type,
                    expression
            )
        }
    }
}