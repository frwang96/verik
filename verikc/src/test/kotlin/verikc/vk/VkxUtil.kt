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
import verikc.kt.KtxUtil
import verikc.vk.ast.*

object VkxUtil {

    fun buildFile(string: String): VkFile {
        val compilationUnit = buildCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun buildModule(string: String): VkModule {
        return buildDeclaration(string) as VkModule
    }

    fun buildPort(string: String): VkPort {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun buildActionBlock(string: String): VkActionBlock {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = buildModule(moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    private fun buildCompilationUnit(string: String): VkCompilationUnit {
        return VkDriver.drive(KtxUtil.resolveCompilationUnit(string))
    }

    private fun buildDeclaration(string: String): VkDeclaration {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = buildFile(fileString)
        if (file.declarations.size != 1) {
            throw IllegalArgumentException("${file.declarations.size} declarations found")
        }
        return file.declarations[0]
    }
}