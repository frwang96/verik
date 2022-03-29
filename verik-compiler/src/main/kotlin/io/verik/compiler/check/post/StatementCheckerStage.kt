/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks that all block expressions only contain SystemVerilog statements.
 */
object StatementCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(StatementCheckerVisitor)
    }

    private object StatementCheckerVisitor : TreeVisitor() {

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
            if (blockExpression.parent is EConstraint) return
            blockExpression.statements.forEach {
                if (!isValid(it)) Messages.INVALID_STATEMENT.on(it)
            }
        }

        private fun isValid(statement: EExpression): Boolean {
            if (statement.serializationKind == SerializationKind.STATEMENT)
                return true
            return when (statement) {
                is ESvUnaryExpression -> statement.kind.isStatement()
                is ESvBinaryExpression ->
                    statement.kind in listOf(SvBinaryOperatorKind.ASSIGN, SvBinaryOperatorKind.ARROW_ASSIGN)
                is ECallExpression -> true
                is EInjectedExpression -> true
                is EIfExpression -> true
                is EEventControlExpression -> true
                else -> false
            }
        }
    }
}
