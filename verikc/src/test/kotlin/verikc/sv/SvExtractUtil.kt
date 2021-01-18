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

package verikc.sv

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ps.PsPassUtil
import verikc.sv.ast.*

object SvExtractUtil {

    fun extractFile(string: String): SvFile {
        val compilationUnit = SvStageDriver.extract(PsPassUtil.passCompilationUnit(string))
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun extractComponent(fileContext: String, string: String): SvComponent {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        return extractFile(fileString).components.last()
    }

    fun extractComponentPort(fileContext: String, string: String): SvPort {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = extractComponent(fileContext, componentString)
        return component.ports.last()
    }

    fun extractComponentProperty(fileContext: String, string: String): SvProperty {
        val componentString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val component = extractComponent(fileContext, componentString)
        return component.properties.last()
    }

    fun extractComponentComponentInstance(
        fileContext: String,
        componentContext: String,
        string: String
    ): SvComponentInstance {
        val componentString = """
            class _m: _module() {
                $componentContext
                $string
            }
        """.trimIndent()
        val component = extractComponent(fileContext, componentString)
        return component.componentInstances.last()
    }

    fun extractComponentActionBlock(fileContext: String, componentContext: String, string: String): SvActionBlock {
        val componentString = """
            class _m: _module() {
                $componentContext
                $string
            }
        """.trimIndent()
        val component = extractComponent(fileContext, componentString)
        return component.actionBlocks.last()
    }

    fun extractComponentActionBlockExpression(
        fileContext: String,
        componentContext: String,
        string: String
    ): SvExpression {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = extractComponentActionBlock(fileContext, componentContext, actionBlockString)
        return actionBlock.block.expressions.last()
    }

    fun extractComponentMethodBlock(fileContext: String, componentContext: String, string: String): SvMethodBlock {
        val componentString = """
            class _m: _module() {
                $componentContext
                $string
            }
        """.trimIndent()
        val component = extractComponent(fileContext, componentString)
        return component.methodBlocks.last()
    }

    fun extractPrimaryProperty(fileContext: String, string: String): SvPrimaryProperty {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        return extractFile(fileString).primaryProperties.last()
    }

    fun extractEnum(fileContext: String, string: String): SvEnum {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        return extractFile(fileString).enums.last()
    }

    fun extractCls(fileContext: String, string: String): SvCls {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        return extractFile(fileString).clses.last()
    }
}