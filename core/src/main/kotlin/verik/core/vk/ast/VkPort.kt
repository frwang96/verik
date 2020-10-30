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

package verik.core.vk.ast

import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.kt.ast.KtAnnotationProperty
import verik.core.kt.ast.KtDeclaration
import verik.core.kt.ast.KtPrimaryProperty
import verik.core.sv.ast.SvPortType

enum class VkPortType {
    INPUT,
    OUTPUT,
    INOUT,
    BUS,
    BUSPORT;

    fun extract(line: Line): SvPortType {
        return when (this) {
            INPUT -> SvPortType.INPUT
            OUTPUT -> SvPortType.OUTPUT
            else -> throw LineException("port type not supported", line)
        }
    }

    companion object {

        operator fun invoke(annotations: List<KtAnnotationProperty>, line: Int): VkPortType {
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
                KtAnnotationProperty.BUS -> BUS
                KtAnnotationProperty.BUSPORT -> BUSPORT
                else -> throw LineException("illegal port type", line)
            }
        }
    }
}

data class VkPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val portType: VkPortType,
        val expression: VkExpression
): VkProperty {

    companion object {

        fun isPort(declaration: KtDeclaration): Boolean {
            return declaration is KtPrimaryProperty && declaration.annotations.any {
                it in listOf(
                        KtAnnotationProperty.INPUT,
                        KtAnnotationProperty.OUTPUT,
                        KtAnnotationProperty.INOUT,
                        KtAnnotationProperty.BUS,
                        KtAnnotationProperty.BUSPORT
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkPort {
            val primaryProperty = declaration.let {
                if (it is KtPrimaryProperty) it
                else throw LineException("base property declaration expected", it)
            }

            val type = primaryProperty.type
                    ?: throw LineException("port has not been assigned a type", declaration)

            val portType = VkPortType(primaryProperty.annotations, primaryProperty.line)

            return VkPort(
                    primaryProperty.line,
                    primaryProperty.identifier,
                    primaryProperty.symbol,
                    type,
                    portType,
                    VkExpression(primaryProperty.expression)
            )
        }
    }
}