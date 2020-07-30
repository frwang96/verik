/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core.vk

import io.verik.core.LinePos
import io.verik.core.LinePosException
import io.verik.core.kt.KtRule
import io.verik.core.kt.KtRuleType
import io.verik.core.sv.SvFile

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