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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object BitConstantTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.project.accept(BitConstantVisitor)
    }

    private fun toHexString(value: String, width: Int, element: EElement): String {
        val valueInt = value.toInt()
        val valueWidth = 32 - valueInt.countLeadingZeroBits()
        if (width == 0) {
            m.error("Bit constant cannot have zero width", element)
            return "1'h0"
        }
        if (width < valueWidth) {
            m.warning("Converting constant $value to width $width results in truncation", element)
            return "1'h0"
        }
        val valueString = valueInt.toString(16)
        val valueStringLength = (width + 3) / 4
        val valueStringPadded = valueString.padStart(valueStringLength, '0')

        val builder = StringBuilder()
        builder.append("$width'h")
        valueStringPadded.forEachIndexed { index, it ->
            builder.append(it)
            val countToEnd = valueStringLength - index - 1
            if (countToEnd > 0 && countToEnd % 4 == 0)
                builder.append("_")
        }
        return builder.toString()
    }

    object BitConstantVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.U_INT) {
                val expression = callExpression.valueArguments[0].expression
                if (expression is EConstantExpression) {
                    val width = callExpression.type.asBitWidth(callExpression)
                    val constantExpression = EConstantExpression(
                        callExpression.location,
                        callExpression.type,
                        toHexString(expression.value, width, callExpression)
                    )
                    callExpression.replace(constantExpression)
                }
            }
        }
    }
}