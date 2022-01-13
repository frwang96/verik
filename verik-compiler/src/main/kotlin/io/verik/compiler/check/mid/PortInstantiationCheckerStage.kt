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

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
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
            Core.Vk.Ubit.F_sli_Int,
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
            if (expression is ECallExpression && expression.reference == Core.Vk.F_nc)
                Messages.UNCONNECTED_INPUT_PORT.on(expression, valueParameter.name)
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
                        in arrayTypeReferences ->
                            checkOutputPortExpression(expression.receiver!!)
                        Core.Vk.F_cat_Any_Any ->
                            expression.valueArguments.forEach { checkOutputPortExpression(it) }
                        Core.Vk.F_nc -> {}
                        else ->
                            Messages.ILLEGAL_OUTPUT_PORT_EXPRESSION.on(expression)
                    }
                }
                else ->
                    Messages.ILLEGAL_OUTPUT_PORT_EXPRESSION.on(expression)
            }
        }
    }
}
