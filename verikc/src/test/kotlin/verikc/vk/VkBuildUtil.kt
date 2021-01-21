/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.rs.RsResolveUtil
import verikc.vk.ast.*

object VkBuildUtil {

    fun buildCompilationUnit(string: String): VkCompilationUnit {
        return VkStageDriver.build(RsResolveUtil.resolveCompilationUnit(string))
    }

    fun buildFile(string: String): VkFile {
        val compilationUnit = buildCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun buildComponent(fileContext: String, string: String): VkComponent {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        return file.components.last()
    }

    fun buildModulePort(fileContext: String, string: String): VkPort {
        val moduleString = """
            class M: Module() {
                $string
            }
        """.trimIndent()
        val module = buildComponent(fileContext, moduleString)
        return module.ports.last()
    }

    fun buildModuleProperty(fileContext: String, string: String): VkProperty {
        val moduleString = """
            class M: Module() {
                $string
            }
        """.trimIndent()
        val module = buildComponent(fileContext, moduleString)
        return module.properties.last()
    }

    fun buildModuleComponentInstance(fileContext: String, moduleContext: String, string: String): VkComponentInstance {
        val moduleString = """
            class M: Module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = buildComponent(fileContext, moduleString)
        return module.componentInstances.last()
    }

    fun buildModuleComponentInstanceConnection(
        fileContext: String,
        moduleContext: String,
        string: String
    ): VkConnection {
        val componentInstance = buildModuleComponentInstance(fileContext, moduleContext, string)
        return componentInstance.connections.last()
    }

    fun buildModuleActionBlock(fileContext: String, string: String): VkActionBlock {
        val moduleString = """
            class M: Module() {
                $string
            }
        """.trimIndent()
        val module = buildComponent(fileContext, moduleString)
        return module.actionBlocks.last()
    }

    fun buildModuleMethodBlock(fileContext: String, string: String): VkMethodBlock {
        val moduleString = """
            class M: Module() {
                $string
            }
        """.trimIndent()
        val module = buildComponent(fileContext, moduleString)
        return module.methodBlocks.last()
    }

    fun buildBusComponentInstance(fileContext: String, busContext: String, string: String): VkComponentInstance {
        val busString = """
            class B: Bus() {
                $busContext
                $string
            }
        """.trimIndent()
        val bus = buildComponent(fileContext, busString)
        return bus.componentInstances.last()
    }

    fun buildEnum(fileContext: String, string: String): VkEnum {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        return file.enums.last()
    }

    fun buildCls(fileContext: String, string: String): VkCls {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        return file.clses.last()
    }
}