package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
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
                else -> throw VkParseException(linePos, "illegal module elaboration type")
            }
        }
    }
}

data class VkModule(val elabType: VkModuleElabType, val isCircuit: Boolean, val name: String,
                    val ports: List<VkPort>, val blocks: List<VkBlock>) {

    fun extract(): SvModule {
        return SvModule(name.drop(1), ports.map { it.extract() })
    }

    companion object {

        operator fun invoke(classDeclaration: VkClassDeclaration): VkModule {
            val elabType = VkModuleElabType(classDeclaration.annotations, classDeclaration.linePos)
            if (classDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException(classDeclaration.linePos, "class modifiers are not permitted here")
            }

            val isCircuit = when (classDeclaration.delegationSpecifierName) {
                "_module" -> false
                "_circuit" -> true
                else -> throw VkParseException(classDeclaration.linePos, "illegal delegation specifier")
            }

            val declarations = if (classDeclaration.body != null) {
                if (classDeclaration.body.type == KtRuleType.ENUM_CLASS_BODY) {
                    throw VkParseException(classDeclaration.linePos, "enum class body is not permitted here")
                } else {
                    classDeclaration.body.getChildAs(KtRuleType.CLASS_MEMBER_DECLARATIONS, VkGrammarException())
                            .getChildrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.getFirstAsRule(VkGrammarException()) }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val ports: ArrayList<VkPort> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw VkParseException(declaration.linePos, "nested classes are not permitted")
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw VkParseException(declaration.linePos, "unsupported function declaration")
                    }
                    is VkPropertyDeclaration -> {
                        if (VkPort.isPort(declaration)) ports.add(VkPort(declaration))
                        else throw VkParseException(declaration.linePos, "unsupported property declaration")
                    }
                }
            }

            return VkModule(elabType, isCircuit, classDeclaration.name, ports, blocks)
        }

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }
    }
}