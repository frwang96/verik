/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rsx

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.kt.KtParseUtil
import verikc.rsx.ast.*
import verikc.rsx.table.RsxSymbolTable

object RsxResolveUtil {

    fun resolveType(fileContext: String, string: String): RsxType {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = resolveFile(fileString)
        return file.types.last()
    }

    fun resolveProperty(fileContext: String, string: String): RsxProperty {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = resolveFile(fileString)
        return file.properties.last()
    }

    fun resolveExpression(fileContext: String, string: String): RsxExpression {
        val functionString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val function = resolveFunction(fileContext, functionString)
        val statement = function.block.statements.last()
        return if (statement is RsxStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    fun resolveSymbolTable(string: String): RsxSymbolTable {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val compilationUnit = RsxStageDriver.build(KtParseUtil.parseCompilationUnit(fileString))
        return RsxStageDriver.resolve(compilationUnit)
    }

    private fun resolveCompilationUnit(string: String): RsxCompilationUnit {
        val compilationUnit = RsxStageDriver.build(KtParseUtil.parseCompilationUnit(string))
        RsxStageDriver.resolve(compilationUnit)
        return compilationUnit
    }

    private fun resolveFile(string: String): RsxFile {
        val compilationUnit = resolveCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun resolveFunction(fileContext: String, string: String): RsxFunction {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = resolveFile(fileString)
        return file.functions.last()
    }
}