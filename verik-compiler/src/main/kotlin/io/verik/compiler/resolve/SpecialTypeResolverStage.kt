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
