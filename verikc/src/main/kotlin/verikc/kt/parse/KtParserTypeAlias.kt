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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTerminal
import verikc.al.ast.AlTree
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtExpression
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtTypeAlias
import verikc.lang.util.LangIdentifierUtil

object KtParserTypeAlias {

    fun parse(functionDeclaration: AlTree, symbolContext: SymbolContext): KtTypeAlias {
        val line = functionDeclaration.find(AlTerminal.FUN).line
        val typeConstructorIdentifier = functionDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val identifier = LangIdentifierUtil.typeIdentifier(typeConstructorIdentifier)
            ?: throw LineException("type constructor should be prefixed with t_", line)
        val symbol = symbolContext.registerSymbol(identifier)

        val typeConstructor = KtFunction(
            line,
            typeConstructorIdentifier,
            symbolContext.registerSymbol(typeConstructorIdentifier),
            listOf(),
            listOf(),
            identifier,
            listOf(),
            null
        )

        if (functionDeclaration.find(AlRule.FUNCTION_VALUE_PARAMETERS).contains(AlRule.FUNCTION_VALUE_PARAMETER))
            throw LineException("type aliases cannot be parameterized", line)
        val functionBody = functionDeclaration.findOrNull(AlRule.FUNCTION_BODY)
            ?: throw LineException("type alias expression expected", line)
        val expression = functionBody.findOrNull(AlRule.EXPRESSION)?.let { KtExpression(it, symbolContext) }
            ?: throw LineException("type alias expression expected", line)

        return KtTypeAlias(line, identifier, symbol, typeConstructor, expression)
    }
}