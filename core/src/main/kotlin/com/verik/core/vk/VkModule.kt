package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvBlock
import com.verik.core.sv.SvContinuousAssignment
import com.verik.core.sv.SvInstance
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
                else -> throw LinePosException("illegal module elaboration type", linePos)
            }
        }
    }
}

data class VkModule(
        val elabType: VkModuleElabType,
        val isCircuit: Boolean,
        val identifier: String,
        val instances: List<VkInstance>,
        val moduleDeclarations: List<VkModuleDeclaration>,
        val blocks: List<VkBlock>,
        val linePos: LinePos
) {

    fun extract(): SvModule {
        if (elabType == VkModuleElabType.EXTERN) {
            throw LinePosException("extern elaboration type not supported", linePos)
        }
        val svIdentifier = identifier.drop(1)

        val instances = instances.map { it.extract() }
        val svPorts: ArrayList<SvInstance> = ArrayList()
        val svInstances: ArrayList<SvInstance> = ArrayList()
        for (instance in instances) {
            if (instance.usageType.isPort()) {
                svPorts.add(instance)
            } else {
                svInstances.add(instance)
            }
        }

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

        return SvModule(svIdentifier, svPorts, svInstances, svModuleDeclarations, svContinuousAssignments, svBlocks, linePos)
    }

    companion object {

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }

        operator fun invoke(classDeclaration: VkClassDeclaration): VkModule {
            val elabType = VkModuleElabType(classDeclaration.annotations, classDeclaration.linePos)
            if (classDeclaration.modifiers.isNotEmpty()) {
                throw LinePosException("class modifiers are not permitted here", classDeclaration.linePos)
            }

            val isCircuit = when (classDeclaration.delegationSpecifierName) {
                "_module" -> false
                "_circuit" -> true
                else -> throw LinePosException("illegal delegation specifier", classDeclaration.linePos)
            }

            val declarations = if (classDeclaration.body != null) {
                if (classDeclaration.body.type == KtRuleType.ENUM_CLASS_BODY) {
                    throw LinePosException("enum class body is not permitted here", classDeclaration.linePos)
                } else {
                    classDeclaration.body.childAs(KtRuleType.CLASS_MEMBER_DECLARATIONS)
                            .childrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.firstAsRule() }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val instances: ArrayList<VkInstance> = ArrayList()
            val moduleDeclarations: ArrayList<VkModuleDeclaration> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw LinePosException("nested classes are not permitted", declaration.linePos)
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw LinePosException("unsupported function declaration", declaration.linePos)
                    }
                    is VkPropertyDeclaration -> {
                        if (VkModuleDeclaration.isModuleDeclaration(declaration)) {
                            moduleDeclarations.add(VkModuleDeclaration(declaration))
                        } else {
                            instances.add(VkInstance(declaration))
                        }
                    }
                }
            }

            return VkModule(elabType, isCircuit, classDeclaration.identifier, instances,
                    moduleDeclarations, blocks, classDeclaration.linePos)
        }
    }
}