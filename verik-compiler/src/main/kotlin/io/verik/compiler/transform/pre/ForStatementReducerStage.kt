/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that reduces for statements to forEach call expressions.
 */
object ForStatementReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ForStatementReducerVisitor)
    }

    private object ForStatementReducerVisitor : TreeVisitor() {

        override fun visitKtForStatement(forStatement: EKtForStatement) {
            super.visitKtForStatement(forStatement)
            val functionLiteralExpression = EFunctionLiteralExpression(
                forStatement.body.location,
                arrayListOf(forStatement.valueParameter),
                forStatement.body
            )
            val callExpression = ECallExpression(
                forStatement.location,
                forStatement.type,
                Core.Kt.Collections.F_forEach_Function,
                forStatement.range,
                false,
                arrayListOf(functionLiteralExpression),
                arrayListOf(forStatement.valueParameter.type.copy())
            )
            forStatement.replace(callExpression)
        }
    }
}
