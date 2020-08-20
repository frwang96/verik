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

import verik.core.al.AlRule
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.kt.resolve.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.main.config.FileConfig
import verik.core.main.config.PkgConfig
import java.io.File

object KtUtil {

    val EXPRESSION_NULL = KtExpressionLiteral(1, null, LiteralValue.fromBoolean(false))

    fun getSymbolContext(): SymbolContext {
        val symbolContext = SymbolContext()
        symbolContext.registerConfigs(
                PkgConfig(File(""), File(""), File(""), "x", null),
                listOf(FileConfig(File(""), File(""), File("")))
        )
        return symbolContext
    }

    fun parseDeclaration(rule: AlRule): KtDeclaration {
        val file = Symbol(1, 1, 0)
        val symbolContext = getSymbolContext()
        val indexer = { symbolContext.nextSymbol(file) }
        return KtDeclaration(rule, indexer)
    }

    fun resolveFile(rule: AlRule): KtFile {
        val symbolContext = getSymbolContext()
        val file = KtFile(rule, Symbol(1, 1, 0), symbolContext)
        val symbolTable = KtSymbolTableBuilder.build(file, symbolContext)
        KtResolver.resolve(file, symbolTable)
        return file
    }

    fun resolveDeclaration(rule: AlRule): KtDeclaration {
        val declaration =  parseDeclaration(rule)
        val symbolTable = KtSymbolTable()
        KtResolverType.resolveDeclaration(declaration, Symbol(1, 1, 0), symbolTable)
        KtResolverFunction.resolveDeclaration(declaration, Symbol(1, 1, 0), symbolTable)
        KtResolverProperty.resolveDeclaration(declaration, Symbol(1, 1, 0), symbolTable)
        KtResolverExpression.resolveDeclaration(declaration, Symbol(1, 1, 0), symbolTable)
        return declaration
    }

    fun resolveDeclarationType(rule: AlRule): KtDeclarationType {
        return resolveDeclaration(rule) as KtDeclarationType
    }

    fun resolveDeclarationFunction(rule: AlRule): KtDeclarationFunction {
        return resolveDeclaration(rule) as KtDeclarationFunction
    }

    fun resolveDeclarationBaseProperty(rule: AlRule): KtDeclarationBaseProperty {
        return resolveDeclaration(rule) as KtDeclarationBaseProperty
    }

    fun resolveExpression(
            rule: AlRule,
            parent: Symbol = Symbol(1, 1, 0),
            symbolTable: KtSymbolTable = KtSymbolTable()
    ): KtExpression {
        val expression = KtExpression(rule)
        KtResolverExpression.resolveExpression(expression, parent, symbolTable)
        return expression
    }
}
