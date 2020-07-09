package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvBlock
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
        val identifier: String,
        val ports: List<VkPort>,
        val moduleDeclarations: List<VkModuleDeclaration>,
        val blocks: List<VkBlock>,
        val linePos: LinePos
) {

    fun extract(): SvModule {
        if (elabType == VkModuleElabType.EXTERN) {
            throw VkExtractException("extern elaboration type not supported", linePos)
        }
        val svIdentifier = identifier.drop(1)
        val svPorts = ports.map { it.extract() }
        val svModuleDeclarations = moduleDeclarations.map { it.extract() }
        val svContinuousAssignments: ArrayList<SvContinuousAssignment> = ArrayList()
        val svBlocks: ArrayList<SvBlock> = ArrayList()
        for (block in blocks) {
            val continuousAssignment = block.extractContinuousAssignment()
            if (continuousAssignment != null) {
                svContinuousAssignments.add(continuousAssignment)
            } else {
                svBlocks.add(block.extractBlock())
            }
        }

        return SvModule(svIdentifier, svPorts, svModuleDeclarations, svContinuousAssignments, svBlocks, linePos)
    }

    companion object {

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }

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
                    classDeclaration.body.childAs(KtRuleType.CLASS_MEMBER_DECLARATIONS)
                            .childrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.firstAsRule() }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val ports: ArrayList<VkPort> = ArrayList()
            val moduleDeclarations: ArrayList<VkModuleDeclaration> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw VkParseException("nested classes are not permitted", declaration.linePos)
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw VkParseException("unsupported function declaration", declaration.linePos)
                    }
                    is VkPropertyDeclaration -> {
                        when {
                            VkPort.isPort(declaration) -> ports.add(VkPort(declaration))
                            VkModuleDeclaration.isModuleDeclaration(declaration) -> {
                                moduleDeclarations.add(VkModuleDeclaration(declaration))
                            }
                            else -> throw VkParseException("unsupported property declaration", declaration.linePos)
                        }
                    }
                }
            }

            return VkModule(elabType, isCircuit, classDeclaration.identifier, ports,
                    moduleDeclarations, blocks, classDeclaration.linePos)
        }
    }
}