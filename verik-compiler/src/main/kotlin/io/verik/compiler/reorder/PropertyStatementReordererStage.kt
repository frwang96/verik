/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that moves property statements to the top of block expressions. SystemVerilog prohibits property declarations
 * that are not at the top of a block expression.
 */
object PropertyStatementReordererStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PropertyStatementReorderVisitor)
    }

    private object PropertyStatementReorderVisitor : TreeVisitor() {

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
            val propertyStatements = ArrayList<EPropertyStatement>()
            val statements = ArrayList<EExpression>()
            blockExpression.statements.forEach {
                if (it is EPropertyStatement) {
                    val initializer = it.property.initializer
                    if (initializer != null) {
                        val statement = EKtBinaryExpression(
                            it.location,
                            Core.Kt.C_Unit.toType(),
                            EReferenceExpression(it.location, it.property.type.copy(), it.property, null, false),
                            initializer,
                            KtBinaryOperatorKind.EQ
                        )
                        statement.parent = blockExpression
                        it.property.initializer = null
                        propertyStatements.add(it)
                        statements.add(statement)
                    } else {
                        propertyStatements.add(it)
                    }
                } else statements.add(it)
            }
            blockExpression.statements = ArrayList(propertyStatements + statements)
        }
    }
}
