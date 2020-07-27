package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvFile

// Copyright (c) 2020 Francis Wang

data class VkFile(val modules: List<VkModule>, val top: VkModule) {

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
            var top: VkModule? = null
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> {
                        if (VkModule.isModule(declaration)) {
                            val module = VkModule(declaration)
                            modules.add(module)
                            if (module.elabType == VkModuleElabType.TOP) {
                                if (top == null) top = module
                                else throw LinePosException("only one top-level module declaration is permitted", module.linePos)
                            }
                        }
                        else throw LinePosException("only module declarations are supported", declaration.linePos)
                    }
                    is VkFunctionDeclaration -> throw LinePosException("top level function declarations not supported", declaration.linePos)
                    is VkPropertyDeclaration -> throw LinePosException("top level property declarations not supported", declaration.linePos)
                }
            }

            return if (top != null) VkFile(modules, top)
            else throw LinePosException("no top-level module declaration specified", LinePos.ZERO)
        }
    }
}