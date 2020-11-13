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

package verik.core.kt.ast

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.al.AlTokenType
import verik.core.base.SymbolIndexer
import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.kt.parse.KtParserTypeIdentifier

data class KtConstructorInvocation(
        override val line: Int,
        val typeIdentifier: String,
        val args: List<KtExpression>,
        var type: Symbol?
): Line {

    companion object {

        operator fun invoke(classDeclaration: AlRule, indexer: SymbolIndexer): KtConstructorInvocation {
            val line = classDeclaration.childAs(AlTokenType.CLASS).line

            val delegationSpecifiers = classDeclaration
                    .childrenAs(AlRuleType.DELEGATION_SPECIFIERS)
                    .flatMap { it.childrenAs(AlRuleType.ANNOTATED_DELEGATION_SPECIFIER) }
                    .map { it.childAs(AlRuleType.DELEGATION_SPECIFIER) }
            if (delegationSpecifiers.isEmpty()) {
                throw LineException("parent type expected", line)
            }
            if (delegationSpecifiers.size > 1) {
                throw LineException("multiple parent types not permitted", line)
            }

            val child = delegationSpecifiers[0].firstAsRule()
            return when (child.type) {
                AlRuleType.CONSTRUCTOR_INVOCATION -> {
                    val typeIdentifier = KtParserTypeIdentifier.parse(child.childAs(AlRuleType.USER_TYPE))
                    val args = child
                            .childAs(AlRuleType.VALUE_ARGUMENTS)
                            .childrenAs(AlRuleType.VALUE_ARGUMENT)
                            .map { it.childAs(AlRuleType.EXPRESSION) }
                            .map { KtExpression(it, indexer) }
                    KtConstructorInvocation(child.line, typeIdentifier, args, null)
                }
                AlRuleType.USER_TYPE -> {
                    val typeIdentifier = KtParserTypeIdentifier.parse(child)
                    KtConstructorInvocation(child.line, typeIdentifier, listOf(), null)
                }
                else -> throw LineException("constructor invocation or user type expected", line)
            }
        }
    }
}