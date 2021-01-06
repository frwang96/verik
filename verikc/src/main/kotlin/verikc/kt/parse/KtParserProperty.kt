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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTerminal
import verikc.al.ast.AlTree
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtExpression
import verikc.kt.ast.KtProperty

object KtParserProperty {

    fun parse(propertyDeclaration: AlTree, symbolContext: SymbolContext): KtProperty {
        val line = if (propertyDeclaration.contains(AlTerminal.VAL)) {
            propertyDeclaration.find(AlTerminal.VAL).line
        } else {
            propertyDeclaration.find(AlTerminal.VAR).line
        }

        val variableDeclaration = propertyDeclaration.find(AlRule.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        KtIdentifierParserUtil.isFunctionOrPropertyIdentifier(identifier, line)
        val symbol = symbolContext.registerSymbol(identifier)

        if (variableDeclaration.contains(AlRule.TYPE)) {
            throw LineException("explicit type declaration not supported", line)
        }

        val annotations = if (propertyDeclaration.contains(AlRule.MODIFIERS)) {
            KtParserAnnotation.parseAnnotationsProperty(propertyDeclaration.find(AlRule.MODIFIERS))
        } else listOf()

        if (!propertyDeclaration.contains(AlRule.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.find(AlRule.EXPRESSION), symbolContext)

        return KtProperty(line, identifier, symbol, annotations, null, expression)
    }
}
