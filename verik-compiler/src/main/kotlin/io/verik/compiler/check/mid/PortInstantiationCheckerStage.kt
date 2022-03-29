/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object PortInstantiationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PortInstantiationCheckerVisitor)
    }

    private object PortInstantiationCheckerVisitor : TreeVisitor() {

        private val arrayTypeReferences = listOf(
            Core.Vk.Ubit.F_get_Int,
            Core.Vk.Ubit.F_get_Int_Int,
            Core.Vk.Unpacked.F_get_Int,
            Core.Vk.Unpacked.F_get_Ubit
        )

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.MAKE) && initializer != null) {
                if (initializer is ECallExpression) {
                    val reference = initializer.reference
                    if (reference is EPrimaryConstructor) {
                        checkPortInstantiations(initializer, reference)
                    }
                }
            }
        }

        private fun checkPortInstantiations(
            callExpression: ECallExpression,
            primaryConstructor: EPrimaryConstructor
        ) {
            if (callExpression.valueArguments.size != primaryConstructor.valueParameters.size)
                Messages.INTERNAL_ERROR.on(
                    callExpression,
                    "Call expression value arguments do not match value parameters"
                )
            callExpression.valueArguments
                .zip(primaryConstructor.valueParameters)
                .forEach { (valueArgument, valueParameter) ->
                    when {
                        valueParameter.hasAnnotationEntry(AnnotationEntries.IN) ->
                            checkInputPortExpression(valueArgument, valueParameter)
                        valueParameter.hasAnnotationEntry(AnnotationEntries.OUT) ->
                            checkOutputPortExpression(valueArgument)
                    }
                }
        }

        private fun checkInputPortExpression(expression: EExpression, valueParameter: EKtValueParameter) {
            if (expression is ECallExpression && expression.reference == Core.Vk.F_nc) {
                Messages.INPUT_PORT_NOT_CONNECTED.on(expression, valueParameter.name)
            }
            if (!valueParameter.isMutable) {
                val inputPortExpressionVisitor = InputPortExpressionVisitor()
                expression.accept(inputPortExpressionVisitor)
                if (!inputPortExpressionVisitor.isConstant) {
                    Messages.INPUT_PORT_NOT_CONSTANT.on(expression, valueParameter.name)
                }
            }
        }

        private fun checkOutputPortExpression(expression: EExpression) {
            when (expression) {
                is EReferenceExpression -> {
                    val reference = expression.reference
                    if (reference is EProperty && !reference.isMutable)
                        Messages.OUTPUT_PORT_NOT_MUTABLE.on(expression, reference.name)
                }
                is ECallExpression -> {
                    when (expression.reference) {
                        in arrayTypeReferences -> checkOutputPortExpression(expression.receiver!!)
                        Core.Vk.F_cat_Any -> expression.valueArguments.forEach { checkOutputPortExpression(it) }
                        Core.Vk.F_nc -> {}
                        else -> Messages.ILLEGAL_OUTPUT_PORT_EXPRESSION.on(expression)
                    }
                }
                else -> Messages.ILLEGAL_OUTPUT_PORT_EXPRESSION.on(expression)
            }
        }

        private class InputPortExpressionVisitor : TreeVisitor() {

            var isConstant = true

            override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
                super.visitReferenceExpression(referenceExpression)
                val reference = referenceExpression.reference
                when {
                    reference is EProperty && reference.isMutable -> isConstant = false
                    reference is EKtValueParameter && reference.isMutable -> isConstant = false
                }
            }
        }
    }
}
