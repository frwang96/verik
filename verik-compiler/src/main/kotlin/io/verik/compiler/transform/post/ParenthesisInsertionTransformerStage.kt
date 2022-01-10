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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Inserts parenthesis to enforce correctness of order of operations. Conservative parenthesis insertion strategy
 * inserts more parenthesis than necessary for readability.
 */
object ParenthesisInsertionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ParenthesisInsertionTransformerVisitor)
    }

    private object ParenthesisInsertionTransformerVisitor : TreeVisitor() {

        private fun getParenthesisKind(expression: EExpression): ParenthesisKind {
            return when (expression) {
                is ESvBinaryExpression -> getBinaryOperatorParenthesisKind(expression.kind)
                is EInlineIfExpression -> ParenthesisKind.INLINE_IF
                else -> ParenthesisKind.SINGULAR
            }
        }

        private fun getBinaryOperatorParenthesisKind(kind: SvBinaryOperatorKind): ParenthesisKind {
            return when (kind) {
                SvBinaryOperatorKind.MUL -> ParenthesisKind.MUL_DIV
                SvBinaryOperatorKind.DIV -> ParenthesisKind.MUL_DIV
                SvBinaryOperatorKind.PLUS -> ParenthesisKind.PLUS_MINUS
                SvBinaryOperatorKind.MINUS -> ParenthesisKind.PLUS_MINUS
                SvBinaryOperatorKind.LTLT -> ParenthesisKind.SHIFT
                SvBinaryOperatorKind.GTGT -> ParenthesisKind.SHIFT
                SvBinaryOperatorKind.GTGTGT -> ParenthesisKind.SHIFT
                SvBinaryOperatorKind.LT -> ParenthesisKind.COMPARE
                SvBinaryOperatorKind.LTEQ -> ParenthesisKind.COMPARE
                SvBinaryOperatorKind.GT -> ParenthesisKind.COMPARE
                SvBinaryOperatorKind.GTEQ -> ParenthesisKind.COMPARE
                SvBinaryOperatorKind.EQEQ -> ParenthesisKind.EQUALITY
                SvBinaryOperatorKind.EXCL_EQ -> ParenthesisKind.EQUALITY
                SvBinaryOperatorKind.AND -> ParenthesisKind.BITWISE_AND
                SvBinaryOperatorKind.XOR -> ParenthesisKind.BITWISE_XOR
                SvBinaryOperatorKind.OR -> ParenthesisKind.BITWISE_OR
                SvBinaryOperatorKind.ANDAND -> ParenthesisKind.LOGICAL_AND
                SvBinaryOperatorKind.OROR -> ParenthesisKind.LOGICAL_OR
                SvBinaryOperatorKind.ASSIGN -> ParenthesisKind.SINGULAR
                SvBinaryOperatorKind.ARROW_ASSIGN -> ParenthesisKind.SINGULAR
            }
        }

        private fun parenthesize(expression: EExpression) {
            val parent = expression.parentNotNull()
            val parenthesizedExpression = EParenthesizedExpression(
                expression.location,
                expression.type.copy(),
                expression
            )
            parent.replaceChildAsExpressionContainer(expression, parenthesizedExpression)
        }

        override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
            super.visitSvBinaryExpression(binaryExpression)
            val kind = getParenthesisKind(binaryExpression)
            if (kind == ParenthesisKind.SINGULAR)
                return
            val leftKind = getParenthesisKind(binaryExpression.left)
            val rightKind = getParenthesisKind(binaryExpression.right)
            if (leftKind != ParenthesisKind.SINGULAR && leftKind != kind)
                parenthesize(binaryExpression.left)
            if (rightKind != ParenthesisKind.SINGULAR)
                parenthesize(binaryExpression.right)
        }

        override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
            super.visitInlineIfExpression(inlineIfExpression)
            val conditionKind = getParenthesisKind(inlineIfExpression.condition)
            val thenExpressionKind = getParenthesisKind(inlineIfExpression.thenExpression)
            val elseExpressionKind = getParenthesisKind(inlineIfExpression.elseExpression)
            if (conditionKind != ParenthesisKind.SINGULAR)
                parenthesize(inlineIfExpression.condition)
            if (thenExpressionKind != ParenthesisKind.SINGULAR)
                parenthesize(inlineIfExpression.thenExpression)
            if (elseExpressionKind != ParenthesisKind.SINGULAR && elseExpressionKind != ParenthesisKind.INLINE_IF)
                parenthesize(inlineIfExpression.elseExpression)
        }

        private enum class ParenthesisKind {
            SINGULAR,
            MUL_DIV,
            PLUS_MINUS,
            SHIFT,
            COMPARE,
            EQUALITY,
            BITWISE_AND,
            BITWISE_XOR,
            BITWISE_OR,
            LOGICAL_AND,
            LOGICAL_OR,
            INLINE_IF
        }
    }
}
