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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.sv.EDelayExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object StatementCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(StatementCheckerVisitor)
    }

    private object StatementCheckerVisitor : TreeVisitor() {

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
            blockExpression.statements.forEach {
                if (!isValid(it)) Messages.INVALID_STATEMENT.on(it)
            }
        }

        private fun isValid(statement: EExpression): Boolean {
            if (statement.serializationType == SerializationType.STATEMENT)
                return true
            return when (statement) {
                is ESvUnaryExpression ->
                    statement.kind.isIncrementOrDecrement()
                is ESvBinaryExpression ->
                    statement.kind in listOf(SvBinaryOperatorKind.ASSIGN, SvBinaryOperatorKind.ARROW_ASSIGN)
                is ECallExpression -> true
                is EInjectedExpression -> true
                is EIfExpression -> true
                is EEventControlExpression -> true
                is EDelayExpression -> true
                else -> false
            }
        }
    }
}
