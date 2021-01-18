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

    fun buildComponentPort(fileContext: String, string: String): VkPort {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = buildComponent(fileContext, componentString)
        return component.ports.last()
    }

    fun buildComponentProperty(fileContext: String, string: String): VkProperty {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = buildComponent(fileContext, componentString)
        return component.properties.last()
    }

    fun buildComponentComponentInstance(fileContext: String, string: String): VkComponentInstance {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = buildComponent(fileContext, componentString)
        return component.componentInstances.last()
    }

    fun buildComponentComponentInstanceConnection(fileContext: String, string: String): VkConnection {
        val componentInstance = buildComponentComponentInstance(fileContext, string)
        return componentInstance.connections.last()
    }

    fun buildComponentActionBlock(fileContext: String, string: String): VkActionBlock {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = buildComponent(fileContext, componentString)
        return component.actionBlocks.last()
    }

    fun buildComponentMethodBlock(fileContext: String, string: String): VkMethodBlock {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = buildComponent(fileContext, componentString)
        return component.methodBlocks.last()
    }

    fun buildBus(fileContext: String, string: String): VkBus {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        return file.busses.last()
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