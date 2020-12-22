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

package verikc.kt.ast

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.parse.KtParserTypeIdentifier

data class KtTypeParent(
    val line: Line,
    val typeIdentifier: String,
    val args: List<KtExpression>,
    var typeSymbol: Symbol?
) {

    companion object {

        operator fun invoke(classDeclaration: AlxTree, symbolContext: SymbolContext): KtTypeParent {
            val line = if (classDeclaration.contains(AlxTerminalIndex.CLASS)) {
                classDeclaration.find(AlxTerminalIndex.CLASS).line
            } else {
                classDeclaration.find(AlxTerminalIndex.OBJECT).line
            }

            val delegationSpecifiers = classDeclaration
                .findAll(AlxRuleIndex.DELEGATION_SPECIFIERS)
                .flatMap { it.findAll(AlxRuleIndex.ANNOTATED_DELEGATION_SPECIFIER) }
                .map { it.find(AlxRuleIndex.DELEGATION_SPECIFIER) }
            if (delegationSpecifiers.isEmpty()) {
                throw LineException("parent type expected", line)
            }
            if (delegationSpecifiers.size > 1) {
                throw LineException("multiple parent types not permitted", line)
            }

            val child = delegationSpecifiers[0].unwrap()
            return when (child.index) {
                AlxRuleIndex.CONSTRUCTOR_INVOCATION -> {
                    val typeIdentifier = KtParserTypeIdentifier.parse(child.find(AlxRuleIndex.USER_TYPE))
                    val args = child
                        .find(AlxRuleIndex.VALUE_ARGUMENTS)
                        .findAll(AlxRuleIndex.VALUE_ARGUMENT)
                        .map { it.find(AlxRuleIndex.EXPRESSION) }
                        .map { KtExpression(it, symbolContext) }
                    KtTypeParent(child.line, typeIdentifier, args, null)
                }
                AlxRuleIndex.USER_TYPE -> {
                    val typeIdentifier = KtParserTypeIdentifier.parse(child)
                    KtTypeParent(child.line, typeIdentifier, listOf(), null)
                }
                else -> throw LineException("constructor invocation or user type expected", line)
            }
        }
    }
}
