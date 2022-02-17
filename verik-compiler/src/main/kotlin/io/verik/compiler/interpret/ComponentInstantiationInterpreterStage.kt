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
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.declaration.sv.EComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.declaration.sv.EPort
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ComponentInstantiationInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val componentInstantiationInterpreterVisitor = ComponentInstantiationInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(componentInstantiationInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ComponentInstantiationInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretAbstractComponentInstantiation(property: EProperty): EDeclaration? {
            val initializer = property.initializer
            return if (initializer is ECallExpression) {
                when (val component = initializer.reference) {
                    is EAbstractContainerComponent ->
                        interpretComponentInstantiation(property, initializer, component)
                    is EModulePort ->
                        interpretModulePortInstantiation(property, initializer, component)
                    is EClockingBlock ->
                        interpretClockingBlockInstantiation(property, initializer, component)
                    else -> null
                }
            } else null
        }

        private fun interpretComponentInstantiation(
            property: EProperty,
            callExpression: ECallExpression,
            component: EAbstractContainerComponent
        ): EComponentInstantiation {
            if (component.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            }

            return EComponentInstantiation(
                property.location,
                property.endLocation,
                property.name,
                property.type,
                property.annotationEntries,
                property.documentationLines,
                callExpression.valueArguments
            )
        }

        private fun interpretModulePortInstantiation(
            property: EProperty,
            callExpression: ECallExpression,
            modulePort: EModulePort
        ): EModulePortInstantiation {
            if (modulePort.ports.size != callExpression.valueArguments.size) {
                Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            }
            modulePort.ports.zip(callExpression.valueArguments).forEach { (port, valueArgument) ->
                checkPortNameMatch(port, valueArgument)
            }

            return EModulePortInstantiation(
                property.location,
                property.endLocation,
                property.name,
                property.type,
                property.annotationEntries,
                property.documentationLines,
                callExpression.valueArguments
            )
        }

        private fun interpretClockingBlockInstantiation(
            property: EProperty,
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
            val eventControlExpression = EEventControlExpression(
                eventExpression.location,
                arrayListOf(eventExpression)
            )

            clockingBlock.ports.zip(valueArguments).forEach { (port, valueArgument) ->
                checkPortNameMatch(port, valueArgument)
            }

            return EClockingBlockInstantiation(
                property.location,
                property.endLocation,
                property.name,
                property.type,
                property.annotationEntries,
                property.documentationLines,
                ArrayList(valueArguments),
                eventControlExpression
            )
        }

        private fun checkPortNameMatch(port: EPort, expression: EExpression) {
            if (expression !is EReferenceExpression ||
                expression.receiver != null ||
                expression.reference.name != port.name
            ) {
                Messages.MISMATCHED_PORT_NAME.on(expression, port.name)
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.hasAnnotationEntry(AnnotationEntries.MAKE)) {
                val interpretedProperty = interpretAbstractComponentInstantiation(property)
                if (interpretedProperty != null) {
                    referenceUpdater.replace(property, interpretedProperty)
                }
            }
        }
    }
}
