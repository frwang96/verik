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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtPropertyStatement
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvPropertyStatement
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object PropertyInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val propertyInterpreterVisitor = PropertyInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(propertyInterpreterVisitor)
        referenceUpdater.flush()
    }

    class PropertyInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        private fun interpret(property: EKtProperty): EElement {
            return interpretModuleInstantiation(property)
                ?: ESvProperty(
                    property.location,
                    property.name,
                    property.type,
                    property.initializer
                )
        }

        private fun interpretModuleInstantiation(property: EKtProperty): EModuleInstantiation? {
            val callExpression = property.initializer
            if (callExpression !is EKtCallExpression)
                return null
            val module = callExpression.reference
            if (module !is EModule)
                return null

            if (module.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
                return null
            }

            val portInstantiations = module.ports
                .zip(callExpression.valueArguments)
                .map { interpretPortInstantiation(it.first, it.second) }
            return EModuleInstantiation(
                property.location,
                property.name,
                property.type,
                portInstantiations
            )
        }

        private fun interpretPortInstantiation(
            port: EPort,
            expression: EExpression
        ): PortInstantiation {
            return if (expression is EKtCallExpression && expression.reference == Core.Vk.F_NC) {
                if (port.portType == PortType.INPUT)
                    Messages.INPUT_PORT_NOT_CONNECTED.on(expression, port.name)
                PortInstantiation(port, null)
            } else {
                PortInstantiation(port, expression)
            }
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            referenceUpdater.replace(property, interpret(property))
        }

        override fun visitKtPropertyStatement(propertyStatement: EKtPropertyStatement) {
            super.visitKtProperty(propertyStatement.property)
            val oldProperty = propertyStatement.property
            val newProperty = ESvProperty(
                oldProperty.location,
                oldProperty.name,
                oldProperty.type,
                oldProperty.initializer,
            )
            propertyStatement.replace(ESvPropertyStatement(propertyStatement.location, newProperty))
            referenceUpdater.update(oldProperty, newProperty)
        }
    }
}
