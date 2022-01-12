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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EComponentInstantiation
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EInjectedProperty
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object PropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val propertyInterpreterVisitor = PropertyInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(propertyInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class PropertyInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        private fun interpret(property: EKtProperty): EDeclaration {
            interpretInjectedProperty(property)?.let { return it }
            interpretAbstractComponentInstantiation(property)?.let { return it }
            val isStatic = when (val parent = property.parent) {
                is ESvClass -> if (parent.isDeclarationsStatic) true else null
                is EPropertyStatement -> false
                is EIsExpression -> false
                else -> null
            }
            return ESvProperty(
                location = property.location,
                name = property.name,
                type = property.type,
                initializer = property.initializer,
                isComAssignment = property.hasAnnotationEntry(AnnotationEntries.COM),
                isMutable = property.isMutable,
                isStatic = isStatic
            )
        }

        private fun interpretInjectedProperty(property: EKtProperty): EInjectedProperty? {
            val initializer = property.initializer
            if (initializer is ECallExpression && initializer.reference == Core.Vk.F_sv_String) {
                val expression = initializer.valueArguments[0]
                if (expression is EStringTemplateExpression) {
                    return EInjectedProperty(
                        property.location,
                        property.name,
                        expression.entries
                    )
                } else if (expression is ECallExpression && expression.reference == Core.Kt.Text.F_trimIndent) {
                    val receiver = expression.receiver!!
                    if (receiver is EStringTemplateExpression) {
                        return EInjectedProperty(
                            property.location,
                            property.name,
                            StringEntry.trimIndent(receiver.entries)
                        )
                    } else {
                        Messages.ILLEGAL_INJECTED_PROPERTY.on(property, property.name)
                    }
                } else {
                    Messages.ILLEGAL_INJECTED_PROPERTY.on(property, property.name)
                }
            }
            return null
        }

        private fun interpretAbstractComponentInstantiation(property: EKtProperty): EDeclaration? {
            if (!property.hasAnnotationEntry(AnnotationEntries.MAKE))
                return null
            return when (val initializer = property.initializer) {
                is ECallExpression -> {
                    when (val component = initializer.reference) {
                        is EAbstractContainerComponent ->
                            interpretComponentInstantiation(property, initializer, component)
                        is EModulePort ->
                            interpretModulePortInstantiation(property, initializer, component)
                        is EClockingBlock ->
                            interpretClockingBlockInstantiation(property, initializer, component)
                        else -> null
                    }
                }
                is ENullExpression -> {
                    ESvProperty(
                        location = property.endLocation,
                        name = property.name,
                        type = property.type,
                        initializer = initializer,
                        isComAssignment = false,
                        isMutable = false,
                        isStatic = null
                    )
                }
                else -> null
            }
        }

        private fun interpretComponentInstantiation(
            property: EKtProperty,
            callExpression: ECallExpression,
            component: EAbstractContainerComponent
        ): EComponentInstantiation {
            if (component.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            }

            val portInstantiations = component.ports
                .zip(callExpression.valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, false) }
            return EComponentInstantiation(
                property.location,
                property.endLocation,
                property.name,
                property.type,
                portInstantiations
            )
        }

        private fun interpretModulePortInstantiation(
            property: EKtProperty,
            callExpression: ECallExpression,
            modulePort: EModulePort
        ): EModulePortInstantiation {
            if (modulePort.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            }

            val portInstantiations = modulePort.ports
                .zip(callExpression.valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, true) }
            return EModulePortInstantiation(
                property.location,
                property.endLocation,
                property.name,
                property.type,
                portInstantiations
            )
        }

        private fun interpretClockingBlockInstantiation(
            property: EKtProperty,
            callExpression: ECallExpression,
            clockingBlock: EClockingBlock
        ): EClockingBlockInstantiation {
            val valueArguments = callExpression
                .valueArguments
                .filterIndexed { index, _ -> index != clockingBlock.eventValueParameterIndex }
            if (clockingBlock.ports.size != valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            }

            val eventExpression = callExpression.valueArguments[clockingBlock.eventValueParameterIndex]
            val eventControlExpression = EEventControlExpression(eventExpression.location, eventExpression)

            val portInstantiations = clockingBlock.ports
                .zip(valueArguments)
                .map { interpretPortInstantiation(it.first, it.second, true) }
            return EClockingBlockInstantiation(
                property.location,
                property.endLocation,
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
                    Messages.MISMATCHED_PORT_NAME.on(expression, port.name)
                }
            }
            return if (expression is ECallExpression && expression.reference == Core.Vk.F_nc) {
                PortInstantiation(expression.location, port, null)
            } else {
                PortInstantiation(expression.location, port, expression)
            }
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            referenceUpdater.replace(property, interpret(property))
        }
    }
}
