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
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EBasicComponentInstantiation
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
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

    private class PropertyInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        private fun interpret(property: EKtProperty): EDeclaration {
            interpretAbstractComponentInstantiation(property)?.let { return it }
            val isStatic = when (val parent = property.parent) {
                is ESvBasicClass -> parent.isDeclarationsStatic
                is EPropertyStatement -> false
                else -> null
            }
            return ESvProperty(
                property.location,
                property.name,
                property.type,
                property.initializer,
                isStatic
            )
        }

        private fun interpretAbstractComponentInstantiation(property: EKtProperty): EAbstractComponentInstantiation? {
            val callExpression = property.initializer
            if (callExpression !is EKtCallExpression)
                return null
            return when (val component = callExpression.reference) {
                is EAbstractContainerComponent ->
                    interpretBasicComponentInstantiation(property, callExpression, component)
                is EModulePort ->
                    interpretModulePortInstantiation(property, callExpression, component)
                is EClockingBlock ->
                    interpretClockingBlockInstantiation(property, callExpression, component)
                else -> null
            }
        }

        private fun interpretBasicComponentInstantiation(
            property: EKtProperty,
            callExpression: EKtCallExpression,
            component: EAbstractContainerComponent
        ): EBasicComponentInstantiation? {
            if (component.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
                return null
            }

            val portInstantiations = component.ports
                .zip(callExpression.valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, false) }
            return EBasicComponentInstantiation(
                property.location,
                property.name,
                property.type,
                portInstantiations
            )
        }

        private fun interpretModulePortInstantiation(
            property: EKtProperty,
            callExpression: EKtCallExpression,
            modulePort: EModulePort
        ): EModulePortInstantiation? {
            if (modulePort.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
                return null
            }

            val portInstantiations = modulePort.ports
                .zip(callExpression.valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, true) }
            return EModulePortInstantiation(
                property.location,
                property.name,
                property.type,
                portInstantiations
            )
        }

        private fun interpretClockingBlockInstantiation(
            property: EKtProperty,
            callExpression: EKtCallExpression,
            clockingBlock: EClockingBlock
        ): EClockingBlockInstantiation? {
            val valueArguments = callExpression
                .valueArguments
                .filterIndexed { index, _ -> index != clockingBlock.eventValueParameterIndex }
            if (clockingBlock.ports.size != valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
                return null
            }

            val eventExpression = callExpression.valueArguments[clockingBlock.eventValueParameterIndex]
            val eventControlExpression = EEventControlExpression(eventExpression.location, eventExpression)

            val portInstantiations = clockingBlock.ports
                .zip(valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, true) }
            return EClockingBlockInstantiation(
                property.location,
                property.name,
                property.type,
                portInstantiations,
                eventControlExpression
            )
        }

        private fun interpretPortInstantiation(
            port: EPort,
            expression: EExpression,
            matchPortName: Boolean
        ): PortInstantiation {
            if (matchPortName) {
                if (expression !is EReferenceExpression ||
                    expression.receiver != null ||
                    expression.reference.name != port.name
                ) {
                    Messages.PORT_INSTANTIATION_NAME_MISMATCH.on(expression, port.name)
                }
            }
            return if (expression is EKtCallExpression && expression.reference == Core.Vk.F_nc) {
                if (port.portType == PortType.INPUT)
                    Messages.INPUT_PORT_NOT_CONNECTED.on(expression, port.name)
                PortInstantiation(port, null, port.portType)
            } else {
                PortInstantiation(port, expression, port.portType)
            }
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            referenceUpdater.replace(property, interpret(property))
        }
    }
}
