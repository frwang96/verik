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

package verik.core.vkx

import verik.core.kt.KtAnnotationProperty
import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationBaseProperty
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.svx.SvxPortType
import verik.core.symbol.Symbol

enum class VkxPortType {
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

    fun extract(line: Line): SvxPortType {
        return when (this) {
            INPUT -> SvxPortType.INPUT
            OUTPUT -> SvxPortType.OUTPUT
            else -> throw LineException("port type not supported", line)
        }
    }

    companion object {

        operator fun invoke(annotations: List<KtAnnotationProperty>, line: Int): VkxPortType {
            if (annotations.isEmpty()) {
                throw LineException("port type annotations expected", line)
            }
            if (annotations.size > 1) {
                throw LineException("illegal port type", line)
            }
            return when (annotations[0]) {
                KtAnnotationProperty.INPUT -> INPUT
                KtAnnotationProperty.OUTPUT -> OUTPUT
                KtAnnotationProperty.INOUT -> INOUT
                KtAnnotationProperty.INTERF -> INTERF
                KtAnnotationProperty.MODPORT -> MODPORT
                else -> throw LineException("illegal port type", line)
            }
        }
    }
}

data class VkxPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val portType: VkxPortType,
        val expression: VkxExpression
): VkxDeclaration {

    companion object {

        fun isPort(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationBaseProperty && declaration.annotations.any {
                it in listOf(
                        KtAnnotationProperty.INPUT,
                        KtAnnotationProperty.OUTPUT,
                        KtAnnotationProperty.INOUT,
                        KtAnnotationProperty.INTERF,
                        KtAnnotationProperty.MODPORT
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkxPort {
            val baseProperty = declaration.let {
                if (it is KtDeclarationBaseProperty) it
                else throw LineException("base property declaration expected", it)
            }
            val portType = VkxPortType(baseProperty.annotations, baseProperty.line)
            return VkxPort(
                    baseProperty.line,
                    baseProperty.identifier,
                    baseProperty.symbol,
                    portType,
                    VkxExpression(baseProperty.expression)
            )
        }
    }
}