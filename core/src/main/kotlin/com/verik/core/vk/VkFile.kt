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
            val packageHeader = kotlinFile.getChildAs(KtRuleType.PACKAGE_HEADER, VkGrammarException())
            if (packageHeader.children.isNotEmpty()) {
                throw VkParseException(kotlinFile.linePos, "package headers are not supported")
            }

            val importList = kotlinFile.getChildAs(KtRuleType.IMPORT_LIST, VkGrammarException())
            if (importList.children.isNotEmpty()) {
                throw VkParseException(kotlinFile.linePos, "import lists are not supported")
            }

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