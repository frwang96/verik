/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object BitConstantReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BitConstantReducerVisitor)
    }

    private object BitConstantReducerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val signed = when (callExpression.reference) {
                Core.Vk.F_u_Int, Core.Vk.F_u_String -> false
                Core.Vk.F_s_Int, Core.Vk.F_s_String -> true
                else -> null
            }
            if (signed != null) {
                val expression = callExpression.valueArguments[0]
                val constantExpression = ConstantNormalizer.normalizeBitConstant(expression, signed)
                if (constantExpression != null) {
                    callExpression.replace(constantExpression)
                }
            }
        }
    }
}
