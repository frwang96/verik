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

import verik.core.main.Line
import verik.core.main.LineException
import verik.core.sv.SvInstanceDeclaration
import verik.core.sv.SvInstancePortType

enum class VkInstancePortType {
    NONE,
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

    fun extract(line: Int): SvInstancePortType {
        return when (this) {
            NONE -> SvInstancePortType.NONE
            INPUT -> SvInstancePortType.INPUT
            OUTPUT -> SvInstancePortType.OUTPUT
            else -> throw LineException("unsupported instance port type", line)
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkPropertyAnnotation>, line: Int): VkInstancePortType {
            return when (annotations.size) {
                0 -> NONE
                1 -> {
                    when (annotations[0]) {
                        VkPropertyAnnotation.INPUT -> INPUT
                        VkPropertyAnnotation.OUTPUT -> OUTPUT
                        VkPropertyAnnotation.INOUT-> INOUT
                        VkPropertyAnnotation.INTERF -> INTERF
                        VkPropertyAnnotation.MODPORT -> MODPORT
                        else -> throw LineException("unsupported instance port type", line)
                    }
                }
                else -> throw LineException("illegal instance port type", line)
            }
        }
    }
}

data class VkInstanceDeclaration(
        override val line: Int,
        val portType: VkInstancePortType,
        val identifier: String,
        val type: VkInstanceType
): Line {

    fun extract(): SvInstanceDeclaration {
        val svPortType =  portType.extract(line)
        val packed = when (type) {
            VkBoolType -> null
            is VkSintType -> type.len
            is VkUintType -> type.len
            is VkNamedType -> throw LineException("illegal instance type ${type.identifier}", this)
            VkUnitType -> throw LineException("instance has not been assigned a type", this)
        }

        return SvInstanceDeclaration(line, svPortType, packed, identifier, listOf())
    }

    companion object {

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkInstanceDeclaration {
            val portType = VkInstancePortType(propertyDeclaration.annotations, propertyDeclaration.line)
            val type = propertyDeclaration.expression.let {
                if (it is VkExpressionCallable) VkInstanceType(it)
                else throw LineException("instance type expected", propertyDeclaration)
            }
            return VkInstanceDeclaration(propertyDeclaration.line, portType, propertyDeclaration.identifier, type)
        }
    }
}