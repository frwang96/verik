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
import verik.core.kt.resolve.KtExpressionResolver
import verik.core.kt.resolve.KtFunctionResolver
import verik.core.kt.resolve.KtPropertyResolver
import verik.core.kt.resolve.KtResolver
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
        KtResolver.resolve(file)
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
        KtFunctionResolver.resolveDeclaration(declaration)
        KtPropertyResolver.resolveDeclaration(declaration)
        KtExpressionResolver.resolveDeclaration(declaration)
        return declaration
    }

    fun resolveDeclarationFunction(rule: AlRule): KtDeclarationFunction {
        return resolveDeclaration(rule) as KtDeclarationFunction
    }

    fun resolveDeclarationBaseProperty(rule: AlRule): KtDeclarationBaseProperty {
        return resolveDeclaration(rule) as KtDeclarationBaseProperty
    }

    fun resolveExpression(rule: AlRule): KtExpression {
        val expression = KtExpression(rule)
        KtExpressionResolver.resolveExpression(expression)
        return expression
    }
}
