package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.sv.SvInstance
import com.verik.core.sv.SvInstanceUsageType
import com.verik.core.sv.SvRange

// Copyright (c) 2020 Francis Wang

enum class VkInstanceUsageType {
    REGULAR,
    INPUT,
    OUTPUT,
    INOUT,
    INTF,
    IPORT;

    fun extract(linePos: LinePos): SvInstanceUsageType {
        return when (this) {
            REGULAR -> SvInstanceUsageType.REGULAR
            INPUT -> SvInstanceUsageType.INPUT
            OUTPUT -> SvInstanceUsageType.OUTPUT
            else -> throw LinePosException("unsupported instance usage type", linePos)
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkPropertyAnnotation>, linePos: LinePos): VkInstanceUsageType {
            return when (annotations.size) {
                0 -> REGULAR
                1 -> {
                    when (annotations[0]) {
                        VkPropertyAnnotation.INPUT -> INPUT
                        VkPropertyAnnotation.OUTPUT -> OUTPUT
                        VkPropertyAnnotation.INOUT-> INOUT
                        VkPropertyAnnotation.INTF -> INTF
                        VkPropertyAnnotation.IPORT -> IPORT
                        else -> throw LinePosException("unsupported instance usage type", linePos)
                    }
                }
                else -> throw LinePosException("illegal instance usage type", linePos)
            }
        }
    }
}

data class VkInstance(
        val usageType: VkInstanceUsageType,
        val identifier: String,
        val type: VkDataType,
        val linePos: LinePos
) {

    fun extract(): SvInstance {
        val svUsageType =  usageType.extract(linePos)
        val packed = when (type) {
            VkBoolType -> listOf()
            is VkSintType -> listOf(SvRange(type.len - 1, 0))
            is VkUintType -> listOf(SvRange(type.len - 1, 0))
            is VkNamedType -> throw LinePosException("illegal instance type ${type.identifier}", linePos)
            VkUnitType -> throw LinePosException("instance has not been assigned a type", linePos)
        }

        return SvInstance(svUsageType, packed, identifier, listOf(), linePos)
    }

    companion object {

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkInstance {
            val usageType = VkInstanceUsageType(propertyDeclaration.annotations, propertyDeclaration.linePos)
            val type = propertyDeclaration.expression.let {
                if (it is VkCallableExpression) VkDataType(it)
                else throw LinePosException("instance type expected", propertyDeclaration.linePos)
            }
            return VkInstance(usageType, propertyDeclaration.identifier, type, propertyDeclaration.linePos)
        }
    }
}