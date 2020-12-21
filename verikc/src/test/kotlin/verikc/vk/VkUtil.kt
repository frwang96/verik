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

package verikc.vk

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.kt.KtUtil
import verikc.vk.ast.*

object VkUtil {

    fun buildCompilationUnit(string: String): VkCompilationUnit {
        return VkDriver.drive(KtUtil.resolveCompilationUnit(string))
    }

    fun buildFile(string: String): VkFile {
        val compilationUnit = buildCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun buildModule(context: String, string: String): VkModule {
        return buildDeclaration(context, string) as VkModule
    }

    fun buildPort(context: String, string: String): VkPort {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(context, moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun buildPrimaryProperty(context: String, string: String): VkPrimaryProperty {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(context, moduleString)
        if (module.primaryProperties.size != 1) {
            throw IllegalArgumentException("${module.primaryProperties.size} primary properties found")
        }
        return module.primaryProperties[0]
    }

    fun buildComponentInstance(context: String, string: String): VkComponentInstance {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(context, moduleString)
        if (module.componentInstances.size != 1) {
            throw IllegalArgumentException("${module.componentInstances.size} component instances found")
        }
        return module.componentInstances[0]
    }

    fun buildConnection(context: String, string: String): VkConnection {
        val componentInstance = buildComponentInstance(context, string)
        if (componentInstance.connections.size != 1) {
            throw IllegalArgumentException("${componentInstance.connections.size} connections found")
        }
        return componentInstance.connections[0]
    }

    fun buildActionBlock(context: String, string: String): VkActionBlock {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(context, moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    fun buildEnum(context: String, string: String): VkEnum {
        return buildDeclaration(context, string) as VkEnum
    }

    private fun buildDeclaration(context: String, string: String): VkDeclaration {
        val fileString = """
            package test
            $context
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        return file.declarations.last()
    }
}