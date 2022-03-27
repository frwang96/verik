/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ConstantExpressionReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ConstantExpressionReducerVisitor)
    }

    private object ConstantExpressionReducerVisitor : TreeVisitor() {

        override fun visitConstantExpression(constantExpression: EConstantExpression) {
            super.visitConstantExpression(constantExpression)
            when (constantExpression.type.reference) {
                Core.Kt.C_Boolean ->
                    constantExpression.replace(ConstantNormalizer.normalizeBoolean(constantExpression))
                Core.Kt.C_Int ->
                    constantExpression.replace(ConstantNormalizer.normalizeInt(constantExpression))
            }
        }
    }
}
