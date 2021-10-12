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
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object ParenthesisInsertionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ParenthesisInsertionTransformerVisitor)
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

    private object ParenthesisInsertionTransformerVisitor : TreeVisitor() {

        private fun getPriority(expression: EExpression): Int {
            // higher priority expressions are prioritized for parenthesis insertion
            return when (expression) {
                is ESvBinaryExpression -> getBinaryOperatorPriority(expression.kind)
                is EInlineIfExpression -> 12
                is EEventExpression -> 1
                else -> 0
            }
        }

        private fun getBinaryOperatorPriority(kind: SvBinaryOperatorKind): Int {
            return when (kind) {
                SvBinaryOperatorKind.MUL -> 2
                SvBinaryOperatorKind.PLUS -> 3
                SvBinaryOperatorKind.MINUS -> 3
                SvBinaryOperatorKind.LTLT -> 4
                SvBinaryOperatorKind.GTGT -> 4
                SvBinaryOperatorKind.GTGTGT -> 4
                SvBinaryOperatorKind.LT -> 5
                SvBinaryOperatorKind.LTEQ -> 5
                SvBinaryOperatorKind.GT -> 5
                SvBinaryOperatorKind.GTEQ -> 5
                SvBinaryOperatorKind.EQEQ -> 6
                SvBinaryOperatorKind.EXCL_EQ -> 6
                SvBinaryOperatorKind.AND -> 7
                SvBinaryOperatorKind.XOR -> 8
                SvBinaryOperatorKind.OR -> 9
                SvBinaryOperatorKind.ANDAND -> 10
                SvBinaryOperatorKind.OROR -> 11
                SvBinaryOperatorKind.ASSIGN -> 13
                SvBinaryOperatorKind.ARROW_ASSIGN -> 13
            }
        }

        override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
            super.visitSvBinaryExpression(binaryExpression)
            val priority = getPriority(binaryExpression)
            if (priority < getPriority(binaryExpression.left))
                parenthesize(binaryExpression.left)
            if (priority <= getPriority(binaryExpression.right))
                parenthesize(binaryExpression.right)
        }

        override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
            super.visitEventControlExpression(eventControlExpression)
            val priority = getPriority(eventControlExpression)
            if (priority < getPriority(eventControlExpression.expression))
                parenthesize(eventControlExpression.expression)
        }
    }
}
