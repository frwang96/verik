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

import io.verik.compiler.ast.element.common.VkCallExpression
import io.verik.compiler.ast.element.common.VkExpression
import io.verik.compiler.ast.element.common.VkValueArgument
import io.verik.compiler.ast.element.kt.VkExpressionStringTemplateEntry
import io.verik.compiler.ast.element.kt.VkLiteralStringTemplateEntry
import io.verik.compiler.ast.element.kt.VkStringTemplateExpression
import io.verik.compiler.ast.element.sv.VkStringExpression
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object StringTemplateExpressionReducer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(StringTemplateExpressionVisitor)
        }
    }

    private fun getFormatSpecifier(expression: VkExpression): String {
        return when (expression.type.reference) {
            CoreClass.Kotlin.INT -> "%d"
            else -> {
                m.error("Unable to get format specifier of type: ${expression.type}", expression)
                ""
            }
        }
    }

    object StringTemplateExpressionVisitor : TreeVisitor() {

        override fun visitStringTemplateExpression(stringTemplateExpression: VkStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            val isStringExpression = stringTemplateExpression.entries.all { it is VkLiteralStringTemplateEntry }
            val builder = StringBuilder()
            val valueArguments = ArrayList<VkValueArgument>()
            for (entry in stringTemplateExpression.entries) {
                when (entry) {
                    is VkLiteralStringTemplateEntry -> {
                        entry.text.toCharArray().forEach {
                            val text = when (it) {
                                '\n' -> "\\n"
                                '\t' -> "\\t"
                                '\\' -> "\\\\"
                                '\"' -> "\\\""
                                '\'' -> "\\\'"
                                '%' -> if (isStringExpression) "%" else "%%"
                                else -> it
                            }
                            builder.append(text)
                        }
                    }
                    is VkExpressionStringTemplateEntry -> {
                        builder.append(getFormatSpecifier(entry.expression))
                        valueArguments.add(VkValueArgument(entry.location, NullDeclaration, entry.expression))
                    }
                }
            }

            val stringExpression = VkStringExpression(
                stringTemplateExpression.location,
                builder.toString()
            )
            if (isStringExpression) {
                stringTemplateExpression.replace(stringExpression)
            } else {
                val stringExpressionValueArgument = VkValueArgument(
                    stringTemplateExpression.location,
                    NullDeclaration,
                    stringExpression
                )
                valueArguments.add(0, stringExpressionValueArgument)
                val callExpression = VkCallExpression(
                    stringTemplateExpression.location,
                    stringTemplateExpression.type,
                    CoreFunction.Sv.SFORMATF,
                    valueArguments
                )
                stringTemplateExpression.replace(callExpression)
            }
        }
    }
}