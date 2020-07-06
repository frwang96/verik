package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvContinuousAssignment
import com.verik.core.sv.SvModule

// Copyright (c) 2020 Francis Wang

enum class VkModuleElabType {
    TOP,
    EXTERN,
    REGULAR;

    companion object {

        operator fun invoke(annotations: List<VkClassAnnotation>, linePos: LinePos): VkModuleElabType {
            return when (annotations.size) {
                0 -> REGULAR
                1 -> {
                    when (annotations[0]) {
                        VkClassAnnotation.TOP -> TOP
                        VkClassAnnotation.EXTERN -> EXTERN
                    }
                }
                else -> throw VkParseException("illegal module elaboration type", linePos)
            }
        }
    }
}

data class VkModule(
        val elabType: VkModuleElabType,
        val isCircuit: Boolean,
        val name: String,
        val ports: List<VkPort>,
        val blocks: List<VkBlock>,
        val linePos: LinePos
) {

    fun extract(): SvModule {
        val continuousAssignments: ArrayList<SvContinuousAssignment> = ArrayList()
        for (block in blocks) {
            val continuousAssignment = block.extractContinuousAssignment()
            if (continuousAssignment != null) {
                continuousAssignments.add(continuousAssignment)
            } else throw VkExtractException("only continuous assignments supported", block.linePos)
        }
        return SvModule(name.drop(1), ports.map { it.extract() }, continuousAssignments, linePos)
    }

    companion object {

        operator fun invoke(classDeclaration: VkClassDeclaration): VkModule {
            val elabType = VkModuleElabType(classDeclaration.annotations, classDeclaration.linePos)
            if (classDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException("class modifiers are not permitted here", classDeclaration.linePos)
            }

            val isCircuit = when (classDeclaration.delegationSpecifierName) {
                "_module" -> false
                "_circuit" -> true
                else -> throw VkParseException("illegal delegation specifier", classDeclaration.linePos)
            }

            val declarations = if (classDeclaration.body != null) {
                if (classDeclaration.body.type == KtRuleType.ENUM_CLASS_BODY) {
                    throw VkParseException("enum class body is not permitted here", classDeclaration.linePos)
                } else {
                    classDeclaration.body.getChildAs(KtRuleType.CLASS_MEMBER_DECLARATIONS)
                            .getChildrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.getFirstAsRule() }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val ports: ArrayList<VkPort> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw VkParseException("nested classes are not permitted", declaration.linePos)
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw VkParseException("unsupported function declaration", declaration.linePos)
                    }
                    is VkPropertyDeclaration -> {
                        if (VkPort.isPort(declaration)) ports.add(VkPort(declaration))
                        else throw VkParseException("unsupported property declaration", declaration.linePos)
                    }
                }
            }

            return VkModule(elabType, isCircuit, classDeclaration.name, ports, blocks, classDeclaration.linePos)
        }

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }
    }
}