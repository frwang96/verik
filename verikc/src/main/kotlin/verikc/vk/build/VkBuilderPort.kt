/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vk.build

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.rs.ast.RsDeclaration
import verikc.rs.ast.RsPrimaryProperty
import verikc.vk.ast.VkExpression
import verikc.vk.ast.VkPort

object VkBuilderPort {

    fun match(declaration: RsDeclaration): Boolean {
        return declaration is RsPrimaryProperty && declaration.annotations.any {
            it in listOf(
                AnnotationProperty.INPUT,
                AnnotationProperty.OUTPUT,
                AnnotationProperty.INOUT,
                AnnotationProperty.BUS,
                AnnotationProperty.BUSPORT
            )
        }
    }

    fun build(declaration: RsDeclaration): VkPort {
        val primaryProperty = declaration.let {
            if (it is RsPrimaryProperty) it
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


    private fun getPortType(annotations: List<AnnotationProperty>, line: Line): PortType {
        if (annotations.isEmpty()) {
            throw LineException("port type annotations expected", line)
        }
        if (annotations.size > 1) {
            throw LineException("illegal port type", line)
        }
        return when (annotations[0]) {
            AnnotationProperty.INPUT -> PortType.INPUT
            AnnotationProperty.OUTPUT -> PortType.OUTPUT
            AnnotationProperty.INOUT -> PortType.INOUT
            AnnotationProperty.BUS -> PortType.BUS
            AnnotationProperty.BUSPORT -> PortType.BUSPORT
            else -> throw LineException("illegal port type", line)
        }
    }
}
