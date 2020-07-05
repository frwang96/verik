package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvFile

// Copyright (c) 2020 Francis Wang

data class VkFile(val module: VkModule) {

    fun extract(): SvFile {
        return SvFile(module.extract())
    }

    companion object {

        operator fun invoke(kotlinFile: KtRule): VkFile {
            val topLevelObjects = kotlinFile.getChildrenAs(KtRuleType.TOP_LEVEL_OBJECT)
            val declarations = topLevelObjects
                    .map { it.getFirstAsRule(VkGrammarException()) }
                    .map { VkDeclaration(it) }
            if (declarations.size != 1) {
                throw VkParseException(kotlinFile.linePos, "single module declaration expected")
            }

            val declaration = declarations[0]
            return if (declaration is VkClassDeclaration && VkModule.isModule(declaration)) {
                VkFile(VkModule(declaration))
            } else {
                throw VkParseException(kotlinFile.linePos, "single module declaration expected")
            }
        }
    }
}