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

package verikc.vk.ast

import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.KtAnnotationProperty
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtPrimaryProperty

data class VkPrimaryProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val expression: VkExpression
): VkProperty {

    companion object {

        fun isPrimaryProperty(declaration: KtDeclaration): Boolean {
            return declaration is KtPrimaryProperty && declaration.annotations.none {
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
            val primaryProperty = declaration.let {
                if (it is KtPrimaryProperty) it
                else throw LineException("primary property declaration expected", it)
            }
            if (primaryProperty.annotations.isNotEmpty()) {
                throw LineException("property annotations are not supported here", primaryProperty)
            }

            val expression = VkExpression(primaryProperty.expression)
            return VkPrimaryProperty(
                    primaryProperty.line,
                    primaryProperty.identifier,
                    primaryProperty.symbol,
                    expression.type,
                    expression
            )
        }
    }
}
