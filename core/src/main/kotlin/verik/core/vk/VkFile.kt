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

package verik.core.vk

import verik.core.main.LineException
import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.sv.SvFile

data class VkFile(val modules: List<VkModule>) {

    fun extract(): SvFile {
        return SvFile(modules.map { it.extract() })
    }

    companion object {

        operator fun invoke(kotlinFile: AlRule): VkFile {
            val topLevelObjects = kotlinFile.childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
            val declarations = topLevelObjects
                    .map { it.firstAsRule() }
                    .map { VkDeclaration(it) }

            val modules: ArrayList<VkModule> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> {
                        if (VkModule.isModule(declaration)) {
                            val module = VkModule(declaration)
                            modules.add(module)
                        }
                        else throw LineException("only module declarations are supported", declaration)
                    }
                    is VkFunctionDeclaration -> throw LineException("top level function declarations not supported", declaration)
                    is VkPropertyDeclaration -> throw LineException("top level property declarations not supported", declaration)
                }
            }

            return VkFile(modules)
        }
    }
}