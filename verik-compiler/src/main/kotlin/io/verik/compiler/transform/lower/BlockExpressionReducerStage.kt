/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object BlockExpressionReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BlockExpressionReducerVisitor)
    }

    private object BlockExpressionReducerVisitor : TreeVisitor() {

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
            val blockExpressionIndexerVisitor = BlockExpressionIndexerVisitor()
            val statements = ArrayList<EExpression>()
            for (statement in blockExpression.statements) {
                if (statement is EBlockExpression) {
                    statements.addAll(statement.statements)
                    continue
                }
                blockExpressionIndexerVisitor.reducibleBlockExpressions.clear()
                statement.accept(blockExpressionIndexerVisitor)
                blockExpressionIndexerVisitor.reducibleBlockExpressions.forEach {
                    reduceBlockExpression(statements, it)
                }
                statements.add(statement)
            }
            statements.forEach { it.parent = blockExpression }
            blockExpression.statements = statements
        }

        private fun reduceBlockExpression(statements: ArrayList<EExpression>, blockExpression: EBlockExpression) {
            if (blockExpression.statements.isEmpty())
                Messages.INTERNAL_ERROR.on(blockExpression, "Unexpected empty block expression")
            statements.addAll(blockExpression.statements.dropLast(1))
            blockExpression.replace(blockExpression.statements.last())
        }
    }

    private class BlockExpressionIndexerVisitor : TreeVisitor() {

        val reducibleBlockExpressions = ArrayList<EBlockExpression>()

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            val parent = blockExpression.parent
            if ((parent is EExpression && parent.childBlockExpressionShouldBeReduced(blockExpression)) ||
                parent is EProperty
            ) {
                reducibleBlockExpressions.add(blockExpression)
            }
        }
    }
}
