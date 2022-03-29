/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import kotlin.math.abs

/**
 * Stage that performs type resolution in the special case where the type of a call expression depends on its value
 * arguments.
 */
object SpecialTypeResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SpecialTypeResolverVisitor)
    }

    object SpecialTypeResolverVisitor : TreeVisitor() {

        private val sliceFunctionDeclarations = listOf(
            Core.Vk.Ubit.F_get_Int_Int,
            Core.Vk.Ubit.F_set_Int_Int_Ubit
        )

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            when (callExpression.reference) {
                in sliceFunctionDeclarations -> {
                    val width = getWidth(callExpression.valueArguments[0], callExpression.valueArguments[1])
                    if (width != null) {
                        callExpression.typeArguments = arrayListOf(Cardinal.of(width).toType())
                    } else {
                        Messages.INDETERMINATE_SLICE_WIDTH.on(callExpression)
                        callExpression.typeArguments = arrayListOf(Cardinal.UNRESOLVED.toType())
                    }
                }
                Core.Vk.F_cluster_Int_Function -> {
                    val size = ConstantNormalizer.parseIntOrNull(callExpression.valueArguments[0])
                    if (size != null) {
                        callExpression.type.arguments[0] = Cardinal.of(size).toType()
                    } else {
                        Messages.EXPRESSION_NOT_CONSTANT.on(callExpression.valueArguments[0])
                    }
                }
            }
        }

        private fun getWidth(startExpression: EExpression, endExpression: EExpression): Int? {
            val startValue = ConstantNormalizer.parseIntOrNull(startExpression) ?: return null
            val endValue = ConstantNormalizer.parseIntOrNull(endExpression) ?: return null
            return abs(startValue - endValue) + 1
        }
    }
}
