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

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlToken
import io.verik.core.al.AlTokenType


class KtPrimaryExpressionParser {

    companion object {

        fun parse(primaryExpression: AlRule): KtExpression {
            val child = primaryExpression.firstAsRule()

            return when(child.type) {
                AlRuleType.PARENTHESIZED_EXPRESSION -> {
                    KtExpressionParser.parse(child.firstAsRule())
                }
                AlRuleType.SIMPLE_IDENTIFIER -> {
                    KtExpressionProperty(primaryExpression.line, null, child.firstAsTokenText())
                }
                AlRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                AlRuleType.STRING_LITERAL -> {
                    parseStringLiteral(child)
                }
                AlRuleType.FUNCTION_LITERAL -> {
                    parseLambdaLiteral(child.childAs(AlRuleType.LAMBDA_LITERAL))
                }
                AlRuleType.THIS_EXPRESSION -> {
                    KtExpressionLiteral(primaryExpression.line, "this")
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    KtExpressionLiteral(primaryExpression.line, "super")
                }
                AlRuleType.IF_EXPRESSION -> {
                    parseIfExpression(child)
                }
                AlRuleType.WHEN_EXPRESSION -> {
                    throw LineException("when expressions are not supported", primaryExpression)
                }
                AlRuleType.JUMP_EXPRESSION -> {
                    parseJumpExpression(child)
                }
                else -> throw LineException("primary expression expected", primaryExpression)
            }
        }

        fun parseLambdaLiteral(lambdaLiteral: AlRule): KtExpression {
            if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
                throw LineException("lambda parameters not supported", lambdaLiteral)
            }
            val statements = lambdaLiteral
                    .childAs(AlRuleType.STATEMENTS)
                    .childrenAs(AlRuleType.STATEMENT)
                    .map { KtStatement(it) }
            val block = KtBlock(lambdaLiteral.line, statements)
            return KtExpressionLambda(lambdaLiteral.line, block)
        }

        private fun parseLiteralConstant(literalConstant: AlRule): KtExpression {
            val value = when (val text = literalConstant.firstAsTokenText()) {
                "true" -> "1"
                "false" -> "0"
                else -> text
            }
            return KtExpressionLiteral(literalConstant.line, value)
        }

        private fun parseStringLiteral(stringLiteral: AlRule): KtExpression {
            val lineStringLiteral = stringLiteral.childAs(AlRuleType.LINE_STRING_LITERAL)
            val segments = lineStringLiteral.children.map {
                val lineStringSegment = it.asRule()
                when (lineStringSegment.type) {
                    AlRuleType.LINE_STRING_CONTENT -> {
                        parseLineStringContent(lineStringSegment.firstAsToken())
                    }
                    AlRuleType.LINE_STRING_EXPRESSION -> {
                        KtStringSegmentExpression(KtExpression(lineStringSegment.firstAsRule()))
                    }
                    else -> throw LineException("line string content or expression expected", lineStringSegment)
                }
            }
            return KtExpressionString(stringLiteral.line, segments)
        }

        private fun parseLineStringContent(lineStringContent: AlToken): KtStringSegment {
            return when (lineStringContent.type) {
                AlTokenType.LINE_STR_TEXT -> {
                    KtStringSegmentLiteral(lineStringContent.text)
                }
                AlTokenType.LINE_STR_ESCAPED_CHAR -> {
                    listOf("\\u", "\\b", "\\r").forEach {
                        if (lineStringContent.text.startsWith(it)) {
                            throw LineException("illegal escape sequence ${lineStringContent.text}", lineStringContent)
                        }
                    }
                    return KtStringSegmentLiteral(
                            when (lineStringContent.text){
                                "\\$" -> "\$"
                                "\\'" -> "\'"
                                else -> lineStringContent.text
                            }
                    )
                }
                AlTokenType.LINE_STR_REF -> {
                    val identifier = lineStringContent.text.drop(1)
                    return KtStringSegmentExpression(
                            KtExpressionProperty(lineStringContent.line, null, identifier)
                    )
                }
                else -> throw LineException("line string content expected", lineStringContent)
            }
        }

        private fun parseIfExpression(ifExpression: AlRule): KtExpression {
            val target = KtExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
            return if (ifExpression.containsType(AlTokenType.ELSE)) {
                var ifBody: KtExpression = KtExpressionLambda(ifExpression.line, KtBlock(ifExpression.line, listOf()))
                var elseBody: KtExpression = KtExpressionLambda(ifExpression.line, KtBlock(ifExpression.line, listOf()))
                var isIf = true
                for (child in ifExpression.children) {
                    if (child is AlToken && child.type == AlTokenType.ELSE) {
                        isIf = false
                    } else if (child is AlRule && child.type == AlRuleType.CONTROL_STRUCTURE_BODY) {
                        if (isIf) {
                            ifBody = KtExpressionLambda(child.line, KtBlock(child.firstAsRule()))
                        } else {
                            elseBody = KtExpressionLambda(child.line, KtBlock(child.firstAsRule()))
                        }
                    }
                }
                KtExpressionFunction(
                        ifExpression.line,
                        target,
                        KtFunctionIdentifierOperator(KtOperatorType.IF_ELSE),
                        listOf(ifBody, elseBody)
                )
            } else {
                val ifBody = if (ifExpression.containsType(AlRuleType.CONTROL_STRUCTURE_BODY)) {
                    val child = ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY)
                    KtExpressionLambda(child.line, KtBlock(child.firstAsRule()))
                } else {
                    KtExpressionLambda(ifExpression.line, KtBlock(ifExpression.line, listOf()))
                }
                KtExpressionFunction(
                        ifExpression.line,
                        target,
                        KtFunctionIdentifierOperator(KtOperatorType.IF),
                        listOf(ifBody)
                )
            }
        }

        private fun parseJumpExpression(jumpExpression: AlRule): KtExpression {
            return when (jumpExpression.firstAsTokenType()) {
                AlTokenType.RETURN -> {
                    if (jumpExpression.containsType(AlRuleType.EXPRESSION)) {
                        KtExpressionFunction(
                                jumpExpression.line,
                                null,
                                KtFunctionIdentifierOperator(KtOperatorType.RETURN),
                                listOf(KtExpression(jumpExpression.childAs(AlRuleType.EXPRESSION)))
                        )
                    } else {
                        KtExpressionFunction(
                                jumpExpression.line,
                                null,
                                KtFunctionIdentifierOperator(KtOperatorType.RETURN_UNIT),
                                listOf()
                        )
                    }
                }
                AlTokenType.CONTINUE -> {
                    KtExpressionFunction(
                            jumpExpression.line,
                            null,
                            KtFunctionIdentifierOperator(KtOperatorType.CONTINUE),
                            listOf()
                    )
                }
                AlTokenType.BREAK -> {
                    KtExpressionFunction(
                            jumpExpression.line,
                            null,
                            KtFunctionIdentifierOperator(KtOperatorType.BREAK),
                            listOf()
                    )
                }
                else -> throw LineException("return or continue or break expected", jumpExpression)
            }
        }
    }
}