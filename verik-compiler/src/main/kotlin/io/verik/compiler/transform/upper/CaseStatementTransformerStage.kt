/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.element.expression.sv.ECaseStatement
import io.verik.compiler.ast.property.CaseEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.BooleanConstantKind
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CaseStatementTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CaseStatementTransformerVisitor)
    }

    private object CaseStatementTransformerVisitor : TreeVisitor() {

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            if (whenExpression.entries.isEmpty()) {
                val blockExpression = EBlockExpression.empty(whenExpression.location)
                whenExpression.replace(blockExpression)
                return
            }
            val subject = whenExpression.subject
                ?: ConstantBuilder.buildBoolean(whenExpression, BooleanConstantKind.TRUE)
            val entries = whenExpression.entries.map {
                CaseEntry(it.conditions, it.body)
            }
            val caseStatement = ECaseStatement(
                whenExpression.location,
                whenExpression.endLocation,
                whenExpression.type,
                subject,
                entries
            )
            whenExpression.replace(caseStatement)
        }
    }
}
