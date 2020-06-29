package com.verik.core.vk

import com.verik.core.LinePos

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
                    else -> throw VkParseException(linePos, "illegal port type")
                }
            } else throw VkParseException(linePos, "illegal port type")
        }
    }
}

data class VkPort(val portType: VkPortType, val name: String, val dataType: VkDataType) {
    companion object {
        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkPort {
            val portType = VkPortType(propertyDeclaration.annotations, propertyDeclaration.linePos)
            if (propertyDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException(propertyDeclaration.linePos, "property modifiers are not permitted here")
            }
            return VkPort(portType, propertyDeclaration.name, propertyDeclaration.dataType)
        }

        fun isPort(propertyDeclaration: VkPropertyDeclaration): Boolean {
            return propertyDeclaration.annotations.any {
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
}