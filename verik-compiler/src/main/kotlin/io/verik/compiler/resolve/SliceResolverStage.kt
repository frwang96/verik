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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import kotlin.math.abs

object SliceResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SliceResolverVisitor)
    }

    object SliceResolverVisitor : TreeVisitor() {

        private val sliceFunctionDeclarations = listOf(
            Core.Vk.Ubit.F_get_Int_Int,
            Core.Vk.Ubit.F_set_Int_Int_Ubit
        )

        private fun getWidth(startExpression: EExpression, endExpression: EExpression): Int? {
            val startValue = ConstantNormalizer.parseIntOrNull(startExpression) ?: return null
            val endValue = ConstantNormalizer.parseIntOrNull(endExpression) ?: return null
            return abs(startValue - endValue) + 1
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference in sliceFunctionDeclarations) {
                val width = getWidth(callExpression.valueArguments[0], callExpression.valueArguments[1])
                if (width != null) {
                    callExpression.typeArguments = arrayListOf(Cardinal.of(width).toType())
                } else {
                    Messages.INDETERMINATE_SLICE_WIDTH.on(callExpression)
                    callExpression.typeArguments = arrayListOf(Cardinal.UNRESOLVED.toType())
                }
            }
        }
    }
}
