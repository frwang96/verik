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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtAnnotationProperty
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtPrimaryProperty

data class VkPort(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeSymbol: Symbol,
    val portType: PortType,
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
                else throw LineException("base property declaration expected", it.line)
            }

            val typeSymbol = primaryProperty.typeSymbol
                ?: throw LineException("port has not been assigned a type", declaration.line)

            val portType = getPortType(primaryProperty.annotations, primaryProperty.line)

            return VkPort(
                primaryProperty.line,
                primaryProperty.identifier,
                primaryProperty.symbol,
                typeSymbol,
                portType,
                VkExpression(primaryProperty.expression)
            )
        }


        private fun getPortType(annotations: List<KtAnnotationProperty>, line: Line): PortType {
            if (annotations.isEmpty()) {
                throw LineException("port type annotations expected", line)
            }
            if (annotations.size > 1) {
                throw LineException("illegal port type", line)
            }
            return when (annotations[0]) {
                KtAnnotationProperty.INPUT -> PortType.INPUT
                KtAnnotationProperty.OUTPUT -> PortType.OUTPUT
                KtAnnotationProperty.INOUT -> PortType.INOUT
                KtAnnotationProperty.BUS -> PortType.BUS
                KtAnnotationProperty.BUSPORT -> PortType.BUSPORT
                else -> throw LineException("illegal port type", line)
            }
        }
    }
}
