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
                    KtExpressionIdentifier(primaryExpression.fileLine, null, child.firstAsTokenText())
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
                    KtExpressionLiteral(primaryExpression.fileLine, "this")
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    KtExpressionLiteral(primaryExpression.fileLine, "super")
                }
                AlRuleType.IF_EXPRESSION -> {
                    parseIfExpression(child)
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
            val block = KtBlock(statements, lambdaLiteral.fileLine)
            return KtExpressionLambda(lambdaLiteral.fileLine, block)
        }

        private fun parseLiteralConstant(literalConstant: AlRule): KtExpression {
            val value = when (val text = literalConstant.firstAsTokenText()) {
                "true" -> "1"
                "false" -> "0"
                else -> text
            }
            return KtExpressionLiteral(literalConstant.fileLine, value)
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
                    else -> throw FileLineException("line string content or expression expected", lineStringSegment.fileLine)
                }
            }
            return KtExpressionString(stringLiteral.fileLine, segments)
        }

        private fun parseLineStringContent(lineStringContent: AlToken): KtStringSegment {
            return when (lineStringContent.type) {
                AlTokenType.LINE_STR_TEXT -> {
                    KtStringSegmentLiteral(lineStringContent.text)
                }
                AlTokenType.LINE_STR_ESCAPED_CHAR -> {
                    listOf("\\u", "\\b", "\\r").forEach {
                        if (lineStringContent.text.startsWith(it)) {
                            throw FileLineException("illegal escape sequence ${lineStringContent.text}", lineStringContent.fileLine)
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
                            KtExpressionIdentifier(
                                    lineStringContent.fileLine,
                                    null,
                                    identifier
                            )
                    )
                }
                else -> throw FileLineException("line string content expected", lineStringContent.fileLine)
            }
        }

        private fun parseIfExpression(ifExpression: AlRule): KtExpression {
            val target = KtExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
            return if (ifExpression.containsType(AlTokenType.ELSE)) {
                var ifBody: KtExpression = KtExpressionLambda(ifExpression.fileLine, KtBlock(listOf(), ifExpression.fileLine))
                var elseBody: KtExpression = KtExpressionLambda(ifExpression.fileLine, KtBlock(listOf(), ifExpression.fileLine))
                var isIf = true
                for (child in ifExpression.children) {
                    if (child is AlToken && child.type == AlTokenType.ELSE) {
                        isIf = false
                    } else if (child is AlRule && child.type == AlRuleType.CONTROL_STRUCTURE_BODY) {
                        if (isIf) {
                            ifBody = parseControlStructureBody(child)
                        } else {
                            elseBody = parseControlStructureBody(child)
                        }
                    }
                }
                KtExpressionFunction(
                        ifExpression.fileLine,
                        target,
                        KtFunctionIdentifierOperator(KtOperatorType.IF_ELSE),
                        listOf(ifBody, elseBody)
                )
            } else {
                val ifBody = if (ifExpression.containsType(AlRuleType.CONTROL_STRUCTURE_BODY)) {
                    parseControlStructureBody(ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY))
                } else {
                    KtExpressionLambda(ifExpression.fileLine, KtBlock(listOf(), ifExpression.fileLine))
                }
                KtExpressionFunction(
                        ifExpression.fileLine,
                        target,
                        KtFunctionIdentifierOperator(KtOperatorType.IF),
                        listOf(ifBody)
                )
            }
        }

        private fun parseControlStructureBody(controlStructureBody: AlRule): KtExpression {
            val block = KtBlock(controlStructureBody.firstAsRule())
            return KtExpressionLambda(controlStructureBody.fileLine, block)
        }
    }
}