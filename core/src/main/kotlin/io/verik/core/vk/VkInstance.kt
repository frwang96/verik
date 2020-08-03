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
import io.verik.core.sv.SvInstance
import io.verik.core.sv.SvInstanceUsageType

enum class VkInstanceUsageType {
    REGULAR,
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

    fun extract(fileLine: FileLine): SvInstanceUsageType {
        return when (this) {
            REGULAR -> SvInstanceUsageType.REGULAR
            INPUT -> SvInstanceUsageType.INPUT
            OUTPUT -> SvInstanceUsageType.OUTPUT
            else -> throw FileLineException("unsupported instance usage type", fileLine)
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkPropertyAnnotation>, lineLine: FileLine): VkInstanceUsageType {
            return when (annotations.size) {
                0 -> REGULAR
                1 -> {
                    when (annotations[0]) {
                        VkPropertyAnnotation.INPUT -> INPUT
                        VkPropertyAnnotation.OUTPUT -> OUTPUT
                        VkPropertyAnnotation.INOUT-> INOUT
                        VkPropertyAnnotation.INTERF -> INTERF
                        VkPropertyAnnotation.MODPORT -> MODPORT
                        else -> throw FileLineException("unsupported instance usage type", lineLine)
                    }
                }
                else -> throw FileLineException("illegal instance usage type", lineLine)
            }
        }
    }
}

data class VkInstance(
        val usageType: VkInstanceUsageType,
        val identifier: String,
        val type: VkDataType,
        val fileLine: FileLine
) {

    fun extract(): SvInstance {
        val svUsageType =  usageType.extract(fileLine)
        val packed = when (type) {
            VkBoolType -> null
            is VkSintType -> type.len
            is VkUintType -> type.len
            is VkNamedType -> throw FileLineException("illegal instance type ${type.identifier}", fileLine)
            VkUnitType -> throw FileLineException("instance has not been assigned a type", fileLine)
        }

        return SvInstance(svUsageType, packed, identifier, listOf(), fileLine)
    }

    companion object {

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkInstance {
            val usageType = VkInstanceUsageType(propertyDeclaration.annotations, propertyDeclaration.fileLine)
            val type = propertyDeclaration.expression.let {
                if (it is VkCallableExpression) VkDataType(it)
                else throw FileLineException("instance type expected", propertyDeclaration.fileLine)
            }
            return VkInstance(usageType, propertyDeclaration.identifier, type, propertyDeclaration.fileLine)
        }
    }
}