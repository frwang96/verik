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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.sv.EBasicComponentInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object PortInstantiationCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PortInstantiationCheckerVisitor)
    }

    private object PortInstantiationCheckerVisitor : TreeVisitor() {

        override fun visitBasicComponentInstantiation(basicComponentInstantiation: EBasicComponentInstantiation) {
            super.visitBasicComponentInstantiation(basicComponentInstantiation)
            basicComponentInstantiation.portInstantiations.forEach {
                if (it.portType == PortType.OUTPUT) {
                    val expression = it.expression
                    if (expression != null)
                        checkOutputPortExpression(it, expression)
                }
            }
        }

        private fun checkOutputPortExpression(portInstantiation: PortInstantiation, expression: EExpression) {
            when (expression) {
                is EReferenceExpression -> {
                    val reference = expression.reference
                    if (reference is ESvProperty && !reference.isMutable)
                        Messages.OUTPUT_PORT_IMMUTABLE_PROPERTY.on(reference, reference.name)
                }
                is ESvArrayAccessExpression -> checkOutputPortExpression(portInstantiation, expression.array)
                is EConstantPartSelectExpression -> checkOutputPortExpression(portInstantiation, expression.array)
                is EConcatenationExpression -> expression.expressions.forEach {
                    checkOutputPortExpression(portInstantiation, it)
                }
                else -> Messages.OUTPUT_PORT_ILLEGAL_EXPRESSION.on(expression, portInstantiation.reference.name)
            }
        }
    }
}
