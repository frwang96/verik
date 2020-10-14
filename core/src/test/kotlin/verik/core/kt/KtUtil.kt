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

package verik.core.kt

import verik.core.al.AlRuleParser
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.base.SymbolIndexer
import verik.core.kt.resolve.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.lang.LangSymbol.SCOPE_LANG
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.main.config.FileConfig
import verik.core.main.config.PkgConfig
import java.io.File

object KtUtil {

    val EXPRESSION_NULL = KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromInt(0))

    fun getSymbolContext(): SymbolContext {
        val symbolContext = SymbolContext()
        symbolContext.registerConfigs(
                PkgConfig(File(""), File(""), File(""), "x", "x_pkg", File("")),
                listOf(FileConfig(File(""), File(""), File(""), File("")))
        )
        return symbolContext
    }

    fun getSymbolTable(): KtSymbolTable {
        val symbolTable = KtSymbolTable()
        val file = KtFile(Symbol(1, 1, 0), listOf(), listOf())
        KtSymbolTableBuilder.buildFile(file, symbolTable, getSymbolContext())
        return symbolTable
    }

    fun parseDeclaration(string: String): KtDeclaration {
        val rule = AlRuleParser.parseDeclaration(string)
        val symbolContext = getSymbolContext()
        val symbolIndexer = SymbolIndexer(Symbol(1, 1, 0), symbolContext)
        return KtDeclaration(rule, symbolIndexer)
    }

    fun parseStatement(string: String): KtStatement {
        val rule = AlRuleParser.parseStatement(string)
        val symbolContext = getSymbolContext()
        val symbolIndexer = SymbolIndexer(Symbol(1, 1, 0), symbolContext)
        return KtStatement(rule, symbolIndexer)
    }

    fun parseExpression(string: String): KtExpression {
        val statement = parseStatement(string) as KtStatementExpression
        return statement.expression
    }

    fun resolveFile(string: String): KtFile {
        val rule = AlRuleParser.parseKotlinFile(string)
        val symbolContext = getSymbolContext()
        val file = KtFile(rule, Symbol(1, 1, 0), symbolContext)
        val symbolTable = getSymbolTable()

        KtResolverTypeSymbol.resolveFile(file, symbolTable)
        KtResolverTypeContent.resolveFile(file, symbolTable)
        KtResolverFunction.resolveFile(file, symbolTable)
        KtResolverProperty.resolveFile(file, symbolTable)
        KtResolverStatement.resolveFile(file, symbolTable)
        return file
    }

    fun resolveDeclaration(declaration: KtDeclaration, file: Symbol, symbolTable: KtSymbolTable) {
        KtResolverTypeSymbol.resolveDeclaration(declaration, file, symbolTable)
        KtResolverTypeContent.resolveDeclaration(declaration, file, symbolTable)
        KtResolverFunction.resolveDeclaration(declaration, file, symbolTable)
        KtResolverProperty.resolveDeclaration(declaration, file, symbolTable)
        KtResolverStatement.resolveDeclaration(declaration, file, symbolTable)
    }

    fun resolveDeclaration(string: String): KtDeclaration {
        val rule = AlRuleParser.parseDeclaration(string)
        val symbolContext = getSymbolContext()
        val file = Symbol(1, 1, 0)
        val symbolIndexer = SymbolIndexer(file, symbolContext)
        val declaration = KtDeclaration(rule, symbolIndexer)
        val symbolTable = getSymbolTable()
        resolveDeclaration(declaration, file, symbolTable)
        return declaration
    }

    fun resolveType(string: String): KtPrimaryType {
        return resolveDeclaration(string) as KtPrimaryType
    }

    fun resolveFunction(string: String): KtPrimaryFunction {
        return resolveDeclaration(string) as KtPrimaryFunction
    }

    fun resolveProperty(string: String): KtPrimaryProperty {
        return resolveDeclaration(string) as KtPrimaryProperty
    }

    fun resolveExpression(string: String): KtExpression {
        val expression = parseExpression(string)
        KtResolverExpression.resolve(expression, SCOPE_LANG, getSymbolTable())
        return expression
    }
}
