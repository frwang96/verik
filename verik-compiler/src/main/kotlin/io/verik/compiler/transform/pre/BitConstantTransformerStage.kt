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
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object BitConstantTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BitConstantTransformerVisitor)
    }

    object BitConstantTransformerVisitor : TreeVisitor() {

        private fun getBitConstantExpression(expression: EConstantExpression): EConstantExpression {
            val value = ConstantUtil.getIntConstantValue(expression.value)
            val width = ConstantUtil.getIntConstantWidth(expression.value)

            val valueStringUndecorated = value.toString(16)
            val valueStringLength = (width + 3) / 4
            val valueStringPadded = valueStringUndecorated.padStart(valueStringLength, '0')

            val builder = StringBuilder()
            builder.append("$width'h")
            valueStringPadded.forEachIndexed { index, it ->
                builder.append(it)
                val countToEnd = valueStringLength - index - 1
                if (countToEnd > 0 && countToEnd % 4 == 0)
                    builder.append("_")
            }
            val valueStringDecorated = builder.toString()

            return EConstantExpression(
                expression.location,
                Core.Vk.C_UBIT.toType(Core.Vk.cardinalOf(width).toType()),
                valueStringDecorated
            )
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.F_U_INT) {
                val expression = callExpression.valueArguments[0]
                if (expression is EConstantExpression) {
                    callExpression.replace(getBitConstantExpression(expression))
                } else {
                    Messages.BIT_CONSTANT_NOT_CONSTANT.on(expression)
                }
            }
        }
    }
}
