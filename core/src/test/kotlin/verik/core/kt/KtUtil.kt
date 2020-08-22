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

    fun parseDeclaration(string: String): KtDeclaration {
        val rule = AlRuleParser.parseDeclaration(string)
        val file = Symbol(1, 1, 0)
        val symbolContext = getSymbolContext()
        return KtDeclaration(rule, file, symbolContext)
    }

    fun parseExpression(string: String): KtExpression {
        val rule = AlRuleParser.parseExpression(string)
        return KtExpression(rule)
    }

    fun resolveFile(string: String): KtFile {
        val rule = AlRuleParser.parseKotlinFile(string)
        val symbolContext = getSymbolContext()
        val file = KtFile(rule, Symbol(1, 1, 0), symbolContext)
        val symbolTable = KtSymbolTableBuilder.build(file, symbolContext)
        KtResolver.resolve(file, symbolTable)
        return file
    }

    fun resolveDeclaration(string: String): KtDeclaration {
        val declaration =  parseDeclaration(string)
        val symbolTable = KtSymbolTable()
        val file = Symbol(1, 1, 0)
        KtResolverType.resolveDeclaration(declaration, file, symbolTable)
        KtResolverFunction.resolveDeclaration(declaration, file, symbolTable)
        KtResolverProperty.resolveDeclaration(declaration, file, symbolTable)
        KtResolverExpression.resolveDeclaration(declaration, file, symbolTable)
        return declaration
    }

    fun resolveDeclarationType(string: String): KtDeclarationType {
        return resolveDeclaration(string) as KtDeclarationType
    }

    fun resolveDeclarationFunction(string: String): KtDeclarationFunction {
        return resolveDeclaration(string) as KtDeclarationFunction
    }

    fun resolveDeclarationBaseProperty(string: String): KtDeclarationBaseProperty {
        return resolveDeclaration(string) as KtDeclarationBaseProperty
    }

    fun resolveExpression(string: String): KtExpression {
        val expression = parseExpression(string)
        KtResolverExpression.resolveExpression(expression, Symbol(1, 1, 0), KtSymbolTable())
        return expression
    }
}
