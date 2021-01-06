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

package verikc.kt

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable

object KtResolveUtil {

    fun resolveCompilationUnit(string: String): KtCompilationUnit {
        val compilationUnit = KtParseUtil.parseCompilationUnit(string)
        KtStageDriver.resolve(compilationUnit)
        return compilationUnit
    }

    fun resolveType(fileContext: String, string: String): KtType {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val compilationUnit = resolveCompilationUnit(fileString)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.types.last()
    }

    fun resolveFunction(fileContext: String, string: String): KtFunction {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val compilationUnit = resolveCompilationUnit(fileString)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.functions.last()
    }

    fun resolveProperty(fileContext: String, string: String): KtProperty {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val compilationUnit = resolveCompilationUnit(fileString)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.properties.last()
    }

    fun resolveSymbolTable(string: String): KtSymbolTable {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val compilationUnit = KtParseUtil.parseCompilationUnit(fileString)
        return KtStageDriver.resolve(compilationUnit)
    }

    fun resolveExpression(fileContext: String, string: String): KtExpression {
        val functionString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val function = resolveFunction(fileContext, functionString)
        val statement = function.block.statements.last()
        return if (statement is KtStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }
}