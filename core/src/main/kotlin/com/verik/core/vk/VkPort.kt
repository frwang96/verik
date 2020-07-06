package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.SvPort
import com.verik.core.sv.SvPortType
import com.verik.core.sv.SvRanges

// Copyright (c) 2020 Francis Wang

enum class VkPortType {
    INPUT,
    OUTPUT,
    INOUTPUT,
    INTF,
    PORT;

    companion object {

        operator fun invoke(annotations: List<VkPropertyAnnotation>, linePos: LinePos): VkPortType {
            return if (annotations.size == 1) {
                when (annotations[0]) {
                    VkPropertyAnnotation.INPUT -> INPUT
                    VkPropertyAnnotation.OUTPUT -> OUTPUT
                    VkPropertyAnnotation.INOUTPUT -> INOUTPUT
                    VkPropertyAnnotation.INTF -> INTF
                    VkPropertyAnnotation.PORT -> PORT
                    else -> throw VkParseException("illegal port type", linePos)
                }
            } else throw VkParseException("illegal port type", linePos)
        }
    }
}

data class VkPort(
        val portType: VkPortType,
        val name: String,
        val dataType: VkDataType,
        val linePos: LinePos
) {

    fun extract(): SvPort {
        val svPortType =  when (portType) {
            VkPortType.INPUT -> SvPortType.INPUT
            VkPortType.OUTPUT -> SvPortType.OUTPUT
            else -> throw VkParseException("unsupported port type", linePos)
        }
        val packed = when (dataType) {
            VkBoolType -> SvRanges(listOf())
            is VkSintType -> SvRanges(listOf(Pair(dataType.len - 1, 0)))
            is VkUintType -> SvRanges(listOf(Pair(dataType.len - 1, 0)))
            VkUnitType -> throw VkExtractException("port has not been assigned a data type", linePos)
        }
        val unpacked = SvRanges(listOf())
        return SvPort(svPortType, packed, name, unpacked, linePos)
    }

    companion object {

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkPort {
            val portType = VkPortType(propertyDeclaration.annotations, propertyDeclaration.linePos)
            return VkPort(portType, propertyDeclaration.name, propertyDeclaration.dataType, propertyDeclaration.linePos)
        }

        fun isPort(propertyDeclaration: VkPropertyDeclaration) = propertyDeclaration.annotations.any {
            it in listOf(
                    VkPropertyAnnotation.INPUT,
                    VkPropertyAnnotation.OUTPUT,
                    VkPropertyAnnotation.INOUTPUT,
                    VkPropertyAnnotation.INTF,
                    VkPropertyAnnotation.PORT
            )
        }
    }
}