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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.property.FunctionQualifierType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object FunctionInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val functionInterpreterVisitor = FunctionInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(functionInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class FunctionInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            val interpretedFunction = interpret(function)
            if (interpretedFunction != null)
                referenceUpdater.replace(function, interpretedFunction)
        }

        private fun interpret(function: EKtFunction): EDeclaration? {
            val body = function.body
            return when {
                function.hasAnnotation(Annotations.COM) -> {
                    if (body != null) {
                        EAlwaysComBlock(function.location, function.name, body)
                    } else {
                        Messages.FUNCTION_MISSING_BODY.on(function, function.name)
                        null
                    }
                }
                function.hasAnnotation(Annotations.SEQ) -> {
                    if (body != null) {
                        getAlwaysSeqBlock(function, body)
                    } else {
                        Messages.FUNCTION_MISSING_BODY.on(function, function.name)
                        null
                    }
                }
                function.hasAnnotation(Annotations.RUN) -> {
                    if (body != null) {
                        EInitialBlock(function.location, function.name, body)
                    } else {
                        Messages.FUNCTION_MISSING_BODY.on(function, function.name)
                        null
                    }
                }
                function.hasAnnotation(Annotations.TASK) -> {
                    val valueParameters = getValueParameters(function.valueParameters, referenceUpdater)
                    ETask(function.location, function.name, function.body, ArrayList(valueParameters))
                }
                else -> {
                    val valueParameters = getValueParameters(function.valueParameters, referenceUpdater)
                    val isStatic = when (val parent = function.parent) {
                        is ESvBasicClass -> parent.isDeclarationsStatic
                        else -> false
                    }
                    val qualifierType = when {
                        function.isAbstract -> FunctionQualifierType.PURE_VIRTUAL
                        function.parent is ESvBasicClass -> {
                            if (isStatic) FunctionQualifierType.REGULAR
                            else FunctionQualifierType.VIRTUAL
                        }
                        else -> FunctionQualifierType.REGULAR
                    }
                    ESvFunction(
                        function.location,
                        function.name,
                        function.type,
                        function.body,
                        isStatic,
                        qualifierType,
                        ArrayList(valueParameters)
                    )
                }
            }
        }

        private fun getAlwaysSeqBlock(function: EKtFunction, body: EExpression): EAlwaysSeqBlock? {
            val onExpression = if (body is EKtBlockExpression) {
                if (body.statements.size == 1) {
                    body.statements[0]
                } else {
                    Messages.ON_EXPRESSION_EXPECTED.on(body)
                    return null
                }
            } else body
            if (onExpression !is EKtCallExpression || onExpression.reference != Core.Vk.F_on_Event_Function) {
                Messages.ON_EXPRESSION_EXPECTED.on(body)
                return null
            }
            val eventExpression = onExpression.valueArguments[0]
            val eventControlExpression = EEventControlExpression(eventExpression.location, eventExpression)
            val alwaysSeqBody = onExpression.valueArguments[1]
                .cast<EFunctionLiteralExpression>()?.body
                ?: return null
            return EAlwaysSeqBlock(
                function.location,
                function.name,
                alwaysSeqBody,
                eventControlExpression
            )
        }

        private fun getValueParameters(
            valueParameters: List<EKtValueParameter>,
            referenceUpdater: ReferenceUpdater
        ): List<ESvValueParameter> {
            return valueParameters.map {
                val valueParameter = ESvValueParameter(it.location, it.name, it.type)
                referenceUpdater.update(it, valueParameter)
                valueParameter
            }
        }
    }
}
