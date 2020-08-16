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
import verik.core.config.FileConfig
import verik.core.config.PkgConfig
import verik.core.kt.resolve.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.symbol.Symbol
import verik.core.symbol.SymbolContext
import java.io.File

object KtUtil {

    fun resolveFile(rule: AlRule): KtFile {
        val symbolContext = SymbolContext()
        symbolContext.registerConfigs(
                PkgConfig(File(""), File(""), File(""), "x", null),
                listOf(FileConfig(File(""), File(""), File("")))
        )
        val file = KtFile(rule, Symbol(1, 1, 0), symbolContext)
        val symbolTable = KtSymbolTableBuilder.build(file)
        KtResolver.resolve(file, symbolTable)
        return file
    }

    fun parseDeclaration(rule: AlRule): KtDeclaration {
        val file = Symbol(1, 1, 0)
        val symbolContext = SymbolContext()
        symbolContext.registerConfigs(
                PkgConfig(File(""), File(""), File(""), "x", null),
                listOf(FileConfig(File(""), File(""), File("")))
        )
        val indexer = { symbolContext.nextSymbol(file) }
        return KtDeclaration(rule, indexer)
    }

    fun resolveDeclaration(rule: AlRule): KtDeclaration {
        val declaration =  parseDeclaration(rule)
        val symbolTable = KtSymbolTable()
        KtFunctionResolver.resolveDeclaration(declaration)
        KtPropertyResolver.resolveDeclaration(declaration, Symbol(1, 1, 0), symbolTable)
        KtExpressionResolver.resolveDeclaration(declaration, symbolTable)
        return declaration
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
        KtExpressionResolver.resolveExpression(expression, parent, symbolTable)
        return expression
    }
}
