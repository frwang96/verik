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

package io.verik.core.kt

import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType


class KtPrimaryExpressionParser {

    companion object {

        fun parse(primaryExpression: AlRule): KtExpression {
            val child = primaryExpression.firstAsRule()

            return when(child.type) {
                AlRuleType.PARENTHESIZED_EXPRESSION -> {
                    KtExpressionParser.parse(child.firstAsRule())
                }
                AlRuleType.SIMPLE_IDENTIFIER -> {
                    KtIdentifierExpression(primaryExpression.fileLine, null, child.firstAsTokenText())
                }
                AlRuleType.LITERAL_CONSTANT -> {
                    KtLiteralExpression(primaryExpression.fileLine, child.firstAsTokenText())
                }
                AlRuleType.STRING_LITERAL -> {
                    throw FileLineException("string literal expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.FUNCTION_LITERAL -> {
                    parseLambdaLiteral(child.childAs(AlRuleType.LAMBDA_LITERAL))
                }
                AlRuleType.THIS_EXPRESSION -> {
                    KtLiteralExpression(primaryExpression.fileLine, "this")
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    KtLiteralExpression(primaryExpression.fileLine, "super")
                }
                AlRuleType.IF_EXPRESSION -> {
                    throw FileLineException("if expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.WHEN_EXPRESSION -> {
                    throw FileLineException("when expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.JUMP_EXPRESSION -> {
                    throw FileLineException("jump expressions are not supported", primaryExpression.fileLine)
                }
                else -> throw FileLineException("primary expression expected", primaryExpression.fileLine)
            }
        }

        fun parseLambdaLiteral(lambdaLiteral: AlRule): KtExpression {
            if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
                throw FileLineException("lambda parameters not supported", lambdaLiteral.fileLine)
            }
            val statements = lambdaLiteral
                    .childAs(AlRuleType.STATEMENTS)
                    .childrenAs(AlRuleType.STATEMENT)
                    .map { KtStatement(it) }
            return KtLambdaExpression(lambdaLiteral.fileLine, statements)
        }
    }
}