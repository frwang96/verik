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

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EInitialBlock
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.property.FunctionQualifierType
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.BooleanConstantKind
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object FunctionInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val functionInterpreterVisitor = FunctionInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(functionInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class FunctionInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            referenceUpdater.replace(function, interpret(function))
        }

        private fun interpret(function: EKtFunction): EDeclaration {
            val body = function.body
            return when {
                function.hasAnnotationEntry(AnnotationEntries.COM) -> {
                    EAlwaysComBlock(
                        function.location,
                        function.name,
                        function.annotationEntries,
                        function.documentationLines,
                        body
                    )
                }
                function.hasAnnotationEntry(AnnotationEntries.SEQ) ->
                    getAlwaysSeqBlock(function)
                function.hasAnnotationEntry(AnnotationEntries.RUN) -> {
                    EInitialBlock(
                        function.location,
                        function.name,
                        function.annotationEntries,
                        function.documentationLines,
                        body
                    )
                }
                function.hasAnnotationEntry(AnnotationEntries.TASK) ->
                    getTask(function)
                else -> getFunction(function)
            }
        }

        private fun getAlwaysSeqBlock(function: EKtFunction): EAlwaysSeqBlock {
            val onExpression = function.body.getOnlyStatement()
            if (onExpression == null) {
                if (!function.body.isEmpty())
                    Messages.EXPECTED_ON_EXPRESSION.on(function)
                return getEmptyAlwaysSeqBlock(function)
            }
            if (onExpression !is ECallExpression || onExpression.reference != Core.Vk.F_on_Event_Event_Function) {
                Messages.EXPECTED_ON_EXPRESSION.on(function)
                return getEmptyAlwaysSeqBlock(function)
            }
            val eventExpressions = onExpression.valueArguments.dropLast(1)
            val eventControlExpression = EEventControlExpression(function.location, ArrayList(eventExpressions))
            val alwaysSeqBody = onExpression.valueArguments.last().cast<EFunctionLiteralExpression>().body
            return EAlwaysSeqBlock(
                function.location,
                function.name,
                function.annotationEntries,
                function.documentationLines,
                alwaysSeqBody,
                eventControlExpression
            )
        }

        private fun getEmptyAlwaysSeqBlock(function: EKtFunction): EAlwaysSeqBlock {
            val body = EBlockExpression.empty(function.location)
            val eventControlExpression = EEventControlExpression(
                function.location,
                arrayListOf(ConstantBuilder.buildBoolean(function.location, BooleanConstantKind.FALSE))
            )
            return EAlwaysSeqBlock(
                function.location,
                function.name,
                function.annotationEntries,
                function.documentationLines,
                body,
                eventControlExpression
            )
        }

        private fun getTask(function: EKtFunction): ETask {
            val valueParameters = ArrayList(getValueParameters(function.valueParameters, referenceUpdater))
            if (function.type.reference != Core.Kt.C_Unit) {
                val valueParameter = ESvValueParameter(
                    location = function.location,
                    name = "<tmp>",
                    type = function.type.copy(),
                    annotationEntries = listOf(),
                    expression = null,
                    isInput = false
                )
                valueParameters.add(valueParameter)
            }
            return ETask(
                function.location,
                function.name,
                function.annotationEntries,
                function.documentationLines,
                function.body,
                function.typeParameters,
                valueParameters
            )
        }

        private fun getFunction(function: EKtFunction): ESvFunction {
            val valueParameters = getValueParameters(function.valueParameters, referenceUpdater)
            val parent = function.parent
            val isStatic = (parent is ESvClass && parent.isObject) || parent is ECompanionObject
            val qualifierType = when {
                function.isAbstract -> FunctionQualifierType.PURE_VIRTUAL
                parent is ESvClass -> {
                    when {
                        parent.isObject -> FunctionQualifierType.REGULAR
                        function.isOverride -> FunctionQualifierType.REGULAR
                        else -> FunctionQualifierType.VIRTUAL
                    }
                }
                else -> FunctionQualifierType.REGULAR
            }
            return ESvFunction(
                function.location,
                function.name,
                function.type,
                function.annotationEntries,
                function.documentationLines,
                function.body,
                function.typeParameters,
                ArrayList(valueParameters),
                qualifierType,
                isStatic
            )
        }

        private fun getValueParameters(
            valueParameters: List<EKtValueParameter>,
            referenceUpdater: ReferenceUpdater
        ): List<ESvValueParameter> {
            return valueParameters.map {
                val valueParameter = ESvValueParameter(
                    it.location,
                    it.name,
                    it.type,
                    it.annotationEntries,
                    it.expression,
                    true
                )
                referenceUpdater.update(it, valueParameter)
                valueParameter
            }
        }
    }
}
