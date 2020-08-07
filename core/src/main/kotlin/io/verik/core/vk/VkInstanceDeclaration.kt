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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.sv.SvInstanceDeclaration
import io.verik.core.sv.SvInstancePortType

enum class VkInstancePortType {
    NONE,
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

    fun extract(fileLine: FileLine): SvInstancePortType {
        return when (this) {
            NONE -> SvInstancePortType.NONE
            INPUT -> SvInstancePortType.INPUT
            OUTPUT -> SvInstancePortType.OUTPUT
            else -> throw FileLineException("unsupported instance port type", fileLine)
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkPropertyAnnotation>, lineLine: FileLine): VkInstancePortType {
            return when (annotations.size) {
                0 -> NONE
                1 -> {
                    when (annotations[0]) {
                        VkPropertyAnnotation.INPUT -> INPUT
                        VkPropertyAnnotation.OUTPUT -> OUTPUT
                        VkPropertyAnnotation.INOUT-> INOUT
                        VkPropertyAnnotation.INTERF -> INTERF
                        VkPropertyAnnotation.MODPORT -> MODPORT
                        else -> throw FileLineException("unsupported instance port type", lineLine)
                    }
                }
                else -> throw FileLineException("illegal instance port type", lineLine)
            }
        }
    }
}

data class VkInstanceDeclaration(
        val portType: VkInstancePortType,
        val identifier: String,
        val type: VkInstanceType,
        val fileLine: FileLine
) {

    fun extract(): SvInstanceDeclaration {
        val svPortType =  portType.extract(fileLine)
        val packed = when (type) {
            VkBoolType -> null
            is VkSintType -> type.len
            is VkUintType -> type.len
            is VkNamedType -> throw FileLineException("illegal instance type ${type.identifier}", fileLine)
            VkUnitType -> throw FileLineException("instance has not been assigned a type", fileLine)
        }

        return SvInstanceDeclaration(svPortType, packed, identifier, listOf(), fileLine)
    }

    companion object {

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkInstanceDeclaration {
            val portType = VkInstancePortType(propertyDeclaration.annotations, propertyDeclaration.fileLine)
            val type = propertyDeclaration.expression.let {
                if (it is VkCallableExpression) VkInstanceType(it)
                else throw FileLineException("instance type expected", propertyDeclaration.fileLine)
            }
            return VkInstanceDeclaration(portType, propertyDeclaration.identifier, type, propertyDeclaration.fileLine)
        }
    }
}