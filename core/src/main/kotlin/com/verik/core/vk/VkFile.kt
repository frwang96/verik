package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvFile

// Copyright (c) 2020 Francis Wang

data class VkFile(val modules: List<VkModule>) {

    fun extract(): SvFile {
        return SvFile(modules.map { it.extract() })
    }

    companion object {

        operator fun invoke(kotlinFile: KtRule): VkFile {
            val topLevelObjects = kotlinFile.childrenAs(KtRuleType.TOP_LEVEL_OBJECT)
            val declarations = topLevelObjects
                    .map { it.firstAsRule() }
                    .map { VkDeclaration(it) }

            val modules: ArrayList<VkModule> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> {
                        if (VkModule.isModule(declaration)) modules.add(VkModule(declaration))
                        else throw VkParseException("only module declarations are supported", declaration.linePos)
                    }
                    is VkFunctionDeclaration -> throw VkParseException("top level function declarations not supported", declaration.linePos)
                    is VkPropertyDeclaration -> throw VkParseException("top level property declarations not supported", declaration.linePos)
                }
            }

            return VkFile(modules)
        }
    }
}