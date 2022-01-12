/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProperty
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
