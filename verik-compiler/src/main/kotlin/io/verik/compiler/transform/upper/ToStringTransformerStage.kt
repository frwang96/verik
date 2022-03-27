/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.Target

object ToStringTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ToStringTransformerVisitor)
    }

    private object ToStringTransformerVisitor : TreeVisitor() {

        private fun transform(expression: EExpression) {
            when (val reference = expression.type.reference) {
                is EEnum -> transformEnum(expression)
                is ESvClass -> transformClass(expression, reference)
            }
        }

        private fun transformEnum(expression: EExpression) {
            if (expression is EReferenceExpression && expression.reference is EEnumEntry) {
                val stringTemplateExpression = EStringTemplateExpression(
                    expression.location,
                    listOf(LiteralStringEntry(expression.reference.name))
                )
                expression.replace(stringTemplateExpression)
            } else {
                val callExpression = ECallExpression(
                    expression.location,
                    Core.Kt.C_String.toType(),
                    Target.F_name,
                    ExpressionCopier.shallowCopy(expression),
                    false,
                    ArrayList(),
                    ArrayList()
                )
                expression.replace(callExpression)
            }
        }

        private fun transformClass(expression: EExpression, cls: ESvClass) {
            val toStringFunction = findToStringFunction(cls)
            if (toStringFunction != null) {
                val callExpression = ECallExpression(
                    expression.location,
                    Core.Kt.C_String.toType(),
                    toStringFunction,
                    ExpressionCopier.shallowCopy(expression),
                    false,
                    ArrayList(),
                    ArrayList()
                )
                expression.replace(callExpression)
            }
        }

        private fun findToStringFunction(cls: ESvClass): ESvFunction? {
            cls.declarations.forEach {
                if (it is ESvFunction && it.name == "toString" && it.valueParameters.isEmpty()) {
                    return it
                }
            }
            val superTypeReference = cls.superType.reference
            return if (superTypeReference is ESvClass) {
                findToStringFunction(superTypeReference)
            } else null
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            stringTemplateExpression.entries.forEach {
                if (it is ExpressionStringEntry) {
                    transform(it.expression)
                }
            }
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            when (callExpression.reference) {
                Core.Kt.Io.F_print_Any -> transform(callExpression.valueArguments[0])
                Core.Kt.Io.F_println_Any -> transform(callExpression.valueArguments[0])
            }
        }
    }
}
