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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EExpressionStringTemplateEntry
import io.verik.compiler.ast.element.common.ELiteralStringTemplateEntry
import io.verik.compiler.ast.element.common.EValueArgument
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object StringTemplateExpressionReducer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.project.accept(StringTemplateExpressionVisitor)
    }

    private fun getFormatSpecifier(expression: EExpression): String {
        return when (expression.type.reference) {
            Core.Kt.BOOLEAN -> "%b"
            Core.Kt.INT -> "%d"
            Core.Vk.UBIT -> "%h"
            else -> {
                m.error("Unable to get format specifier of type: ${expression.type}", expression)
                ""
            }
        }
    }

    object StringTemplateExpressionVisitor : TreeVisitor() {

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            val isStringExpression = stringTemplateExpression.entries.all { it is ELiteralStringTemplateEntry }
            val builder = StringBuilder()
            val valueArguments = ArrayList<EValueArgument>()
            for (entry in stringTemplateExpression.entries) {
                when (entry) {
                    is ELiteralStringTemplateEntry -> {
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
                    is EExpressionStringTemplateEntry -> {
                        builder.append(getFormatSpecifier(entry.expression))
                        valueArguments.add(EValueArgument(entry.location, NullDeclaration, entry.expression))
                    }
                }
            }

            val stringExpression = EStringExpression(
                stringTemplateExpression.location,
                builder.toString()
            )
            if (isStringExpression) {
                stringTemplateExpression.replace(stringExpression)
            } else {
                val stringExpressionValueArgument = EValueArgument(
                    stringTemplateExpression.location,
                    NullDeclaration,
                    stringExpression
                )
                valueArguments.add(0, stringExpressionValueArgument)
                val callExpression = EKtCallExpression(
                    stringTemplateExpression.location,
                    stringTemplateExpression.type,
                    Core.Sv.SFORMATF,
                    null,
                    valueArguments,
                    arrayListOf()
                )
                stringTemplateExpression.replace(callExpression)
            }
        }
    }
}