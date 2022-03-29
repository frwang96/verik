/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.sv.EStringExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.Target

/**
 * Stage that converts string template expressions into sformatf call expressions.
 */
object StringTemplateExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(StringTemplateExpressionTransformerVisitor)
    }

    private object StringTemplateExpressionTransformerVisitor : TreeVisitor() {

        private fun getFormatSpecifier(expression: EExpression): String {
            val type = expression.type
            return when (type.reference) {
                Target.C_Boolean -> "%b"
                Target.C_Int -> "%0d"
                Target.C_Double -> "%f"
                Target.C_String -> "%s"
                Target.C_Ubit, Target.C_Sbit -> {
                    if (type.asBitWidth(expression) < ConstantBuilder.MIN_HEX_CONSTANT_WIDTH) "0b%b" else "0x%h"
                }
                Target.C_Time -> "%0t"
                else -> "%p"
            }
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            val isStringExpression = stringTemplateExpression.entries.all { it is LiteralStringEntry }
            val builder = StringBuilder()
            val valueArguments = ArrayList<EExpression>()
            for (entry in stringTemplateExpression.entries) {
                when (entry) {
                    is LiteralStringEntry -> {
                        entry.text.toCharArray().forEach {
                            val text = when (it) {
                                '\n' -> "\\n"
                                '\t' -> "\\t"
                                '\\' -> "\\\\"
                                '\"' -> "\\\""
                                '%' -> if (isStringExpression) "%" else "%%"
                                else -> it
                            }
                            builder.append(text)
                        }
                    }
                    is ExpressionStringEntry -> {
                        builder.append(getFormatSpecifier(entry.expression))
                        valueArguments.add(entry.expression)
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
                valueArguments.add(0, stringExpression)
                val callExpression = ECallExpression(
                    stringTemplateExpression.location,
                    Target.C_String.toType(),
                    Target.F_sformatf,
                    null,
                    false,
                    valueArguments,
                    ArrayList()
                )
                stringTemplateExpression.replace(callExpression)
            }
        }
    }
}
