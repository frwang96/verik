package com.verik.core.vk

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

data class VkConnection(val src: String, val dst: String)

data class VkModuleDeclaration (
        val identifier: String,
        val moduleType: VkDataType,
        val connections: List<VkConnection>,
        val linePos: LinePos
) {

    companion object {

        fun isModuleDeclaration(propertyDeclaration: VkPropertyDeclaration) =
                propertyDeclaration.annotations.any { it == VkPropertyAnnotation.COMP }

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkModuleDeclaration {
            if (propertyDeclaration.annotations.size != 1
                    || propertyDeclaration.annotations[0] != VkPropertyAnnotation.COMP) {
                throw VkParseException("illegal declaration annotations", propertyDeclaration.linePos)
            }

            val moduleType = VkDataType(propertyDeclaration.expression)

            return VkModuleDeclaration(propertyDeclaration.identifier, moduleType, listOf(), propertyDeclaration.linePos)
        }
    }
}