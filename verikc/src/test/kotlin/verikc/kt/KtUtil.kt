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

package verikc.kt

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.al.AlUtil
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable

object KtUtil {

    fun parsePkg(string: String): KtPkg {
        val compilationUnit = parseCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL)
    }

    fun parseFile(string: String): KtFile {
        val compilationUnit = parseCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun parseDeclaration(string: String): KtDeclaration {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = parseFile(fileString)
        if (file.declarations.size != 1) {
            throw IllegalArgumentException("${file.declarations.size} declarations found")
        }
        return file.declarations[0]
    }

    fun parseStatement(string: String): KtStatement {
        val declarationString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val declaration = parseDeclaration(declarationString)
        val statements = (declaration as KtFunction).block.statements
        if (statements.size != 1) {
            throw IllegalArgumentException("${statements.size} statements found")
        }
        return statements[0]
    }

    fun parseExpression(string: String): KtExpression {
        val statement = parseStatement(string)
        return if (statement is KtStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    fun resolveCompilationUnit(string: String): KtCompilationUnit {
        val compilationUnit = parseCompilationUnit(string)
        KtDriver.resolve(compilationUnit)
        return compilationUnit
    }

    fun resolveDeclaration(fileContext: String, string: String): KtDeclaration {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val compilationUnit = resolveCompilationUnit(fileString)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.declarations.last()
    }

    fun resolveExpression(fileContext: String, string: String): KtExpression {
        val declarationString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val declaration = resolveDeclaration(fileContext, declarationString)
        val statement = (declaration as KtFunction).block.statements.last()
        return if (statement is KtStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    fun buildSymbolTable(string: String): KtSymbolTable {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val compilationUnit = parseCompilationUnit(fileString)
        val symbolTable = KtSymbolTable()
        KtDriver.resolve(compilationUnit, symbolTable)
        return symbolTable
    }

    private fun parseCompilationUnit(string: String): KtCompilationUnit {
        val symbolContext = SymbolContext()
        symbolContext.registerSymbol("test")
        symbolContext.registerSymbol("test/test.kt")
        return KtDriver.parse(AlUtil.parseCompilationUnit(string), symbolContext)
    }
}