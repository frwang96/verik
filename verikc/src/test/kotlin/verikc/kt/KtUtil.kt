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

import verikc.al.AlRuleParser
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.config.FileConfig
import verikc.base.config.PkgConfig
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.lang.LangSymbol.TYPE_INT
import java.io.File

object KtUtil {

    val EXPRESSION_NULL = KtExpressionLiteral(Line(0), TYPE_INT, LiteralValue.fromInt(0))

    fun getFileConfig(): FileConfig {
        return FileConfig(
            File(""),
            File(""),
            File(""),
            File(""),
            Symbol(2),
            Symbol(1)
        )
    }

    fun getSymbolContext(): SymbolContext {
        val symbolContext = SymbolContext()
        symbolContext.registerSymbol("base")
        symbolContext.registerSymbol("base/base.kt")
        return symbolContext
    }

    fun getSymbolTable(): KtSymbolTable {
        val symbolTable = KtSymbolTable()
        KtSymbolTableBuilder.buildFile(getPkgConfig(), getFileConfig(), symbolTable)
        return symbolTable
    }

    fun parseDeclaration(string: String): KtDeclaration {
        val rule = AlRuleParser.parseDeclaration(string)
        val symbolContext = getSymbolContext()
        return KtDeclaration(rule, symbolContext)
    }

    fun parseStatement(string: String): KtStatement {
        val rule = AlRuleParser.parseStatement(string)
        val symbolContext = getSymbolContext()
        return KtStatement(rule, symbolContext)
    }

    fun parseExpression(string: String): KtExpression {
        val statement = parseStatement(string) as KtStatementExpression
        return statement.expression
    }

    fun resolveFile(string: String): KtFile {
        return resolveFileWithIntermediates(string).first
    }

    fun resolveDeclaration(declaration: KtDeclaration, file: Symbol, symbolTable: KtSymbolTable) {
        KtResolverTypeSymbol.resolveDeclaration(declaration, file, symbolTable)
        KtResolverTypeContent.resolveDeclaration(declaration, file, symbolTable)
        KtResolverFunction.resolveDeclaration(declaration, file, symbolTable)
        KtResolverProperty.resolveDeclaration(declaration, file, symbolTable)
        KtResolverStatement.resolveDeclaration(declaration, file, symbolTable)
    }

    fun resolveDeclaration(string: String, declarations: String): KtDeclaration {
        val fileString = """
            package base
            $declarations
        """.trimIndent()
        val intermediates = resolveFileWithIntermediates(fileString)
        val symbolTable = intermediates.second
        val symbolContext = intermediates.third

        val rule = AlRuleParser.parseDeclaration(string)
        val declaration = KtDeclaration(rule, symbolContext)
        resolveDeclaration(declaration, Symbol(2), symbolTable)
        return declaration
    }

    fun resolveDeclaration(string: String): KtDeclaration {
        return resolveDeclaration(string, "")
    }

    fun resolveExpression(string: String): KtExpression {
        val expression = parseExpression(string)
        KtResolverExpression.resolve(expression, SCOPE_LANG, getSymbolTable())
        return expression
    }

    private fun getPkgConfig(): PkgConfig {
        return PkgConfig(
            File(""),
            File(""),
            File(""),
            "base",
            "base_pkg",
            Symbol(1),
            listOf(getFileConfig())
        )
    }

    private fun resolveFileWithIntermediates(string: String): Triple<KtFile, KtSymbolTable, SymbolContext> {
        val rule = AlRuleParser.parseKotlinFile(Symbol.NULL, string)
        val symbolContext = getSymbolContext()
        val file = KtFile(rule, getFileConfig(), symbolContext)
        val symbolTable = getSymbolTable()

        KtResolverTypeSymbol.resolveFile(file, symbolTable)
        KtResolverTypeContent.resolveFile(file, symbolTable)
        KtResolverFunction.resolveFile(file, symbolTable)
        KtResolverProperty.resolveFile(file, symbolTable)
        KtResolverStatement.resolveFile(file, symbolTable)

        return Triple(file, symbolTable, symbolContext)
    }
}
