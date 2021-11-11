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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.Cardinal
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object BitConstantTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BitConstantTransformerVisitor)
    }

    private object BitConstantTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val signed = when (callExpression.reference) {
                Core.Vk.F_u_Int, Core.Vk.F_u_String -> false
                Core.Vk.F_s_Int, Core.Vk.F_s_String -> true
                else -> null
            }
            if (signed != null) {
                val expression = callExpression.valueArguments[0]
                val bitConstant = ConstantUtil.normalizeBitConstant(expression, signed)
                if (bitConstant != null) {
                    val type = if (signed) Core.Vk.C_Sbit.toType(Cardinal.of(bitConstant.width).toType())
                    else Core.Vk.C_Ubit.toType(Cardinal.of(bitConstant.width).toType())
                    val constantExpression = EConstantExpression(
                        expression.location,
                        type,
                        ConstantUtil.formatBitConstant(bitConstant)
                    )
                    callExpression.replace(constantExpression)
                }
            }
        }
    }
}
