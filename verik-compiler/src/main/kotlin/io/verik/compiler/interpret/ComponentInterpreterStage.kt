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

import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ComponentInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val componentInterpreterVisitor = ComponentInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(componentInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ComponentInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val classType = `class`.type
            when {
                classType.isSubtype(Core.Vk.C_Module.toType()) -> {
                    val module = interpretModule(`class`)
                    referenceUpdater.replace(`class`, module)
                    `class`.primaryConstructor?.let { referenceUpdater.update(it, module) }
                }
                classType.isSubtype(Core.Vk.C_ModuleInterface.toType()) -> {
                    val moduleInterface = interpretModuleInterface(`class`)
                    referenceUpdater.replace(`class`, moduleInterface)
                    `class`.primaryConstructor?.let { referenceUpdater.update(it, moduleInterface) }
                }
                classType.isSubtype(Core.Vk.C_ModulePort.toType()) -> {
                    val modulePort = interpretModulePort(`class`)
                    referenceUpdater.replace(`class`, modulePort)
                    `class`.primaryConstructor?.let { referenceUpdater.update(it, modulePort) }
                }
                classType.isSubtype(Core.Vk.C_ClockingBlock.toType()) -> {
                    val clockingBlock = interpretClockingBlock(`class`)
                    referenceUpdater.replace(`class`, clockingBlock)
                    `class`.primaryConstructor?.let { referenceUpdater.update(it, clockingBlock) }
                }
            }
        }

        private fun interpretModule(`class`: EKtClass): EModule {
            val ports = interpretPorts(`class`.primaryConstructor?.valueParameters, referenceUpdater)
            val isSynthesisTop = `class`.hasAnnotation(Annotations.SYNTHESIS_TOP)
            val isSimulationTop = `class`.hasAnnotation(Annotations.SIMULATION_TOP)
            when {
                isSynthesisTop -> if (`class`.isObject)
                    Messages.SYNTHESIS_TOP_IS_OBJECT.on(`class`)
                isSimulationTop -> if (!`class`.isObject)
                    Messages.SIMULATION_TOP_NOT_OBJECT.on(`class`)
                else -> if (`class`.isObject)
                    Messages.MODULE_IS_OBJECT.on(`class`)
            }
            return EModule(
                `class`.location,
                `class`.bodyStartLocation,
                `class`.bodyEndLocation,
                `class`.name,
                `class`.type,
                ports,
                `class`.declarations,
                isSynthesisTop,
                isSimulationTop
            )
        }

        private fun interpretModuleInterface(`class`: EKtClass): EModuleInterface {
            val ports = interpretPorts(`class`.primaryConstructor?.valueParameters, referenceUpdater)
            return EModuleInterface(
                `class`.location,
                `class`.bodyStartLocation,
                `class`.bodyEndLocation,
                `class`.name,
                `class`.type,
                ports,
                `class`.declarations
            )
        }

        private fun interpretModulePort(`class`: EKtClass): EModulePort {
            val ports = interpretPorts(`class`.primaryConstructor?.valueParameters, referenceUpdater)
            return EModulePort(
                `class`.location,
                `class`.bodyStartLocation,
                `class`.bodyEndLocation,
                `class`.name,
                `class`.type,
                ports,
                null
            )
        }

        private fun interpretClockingBlock(`class`: EKtClass): EClockingBlock {
            val valueParameters = `class`.primaryConstructor?.valueParameters
            val eventValueParameter = valueParameters?.find { it.name == "event" }
                ?: Messages.INTERNAL_ERROR.on(`class`, "Could not identify clocking block event value parameter")
            val eventValueParameterIndex = valueParameters.indexOf(eventValueParameter)
            valueParameters.remove(eventValueParameter)

            val ports = interpretPorts(valueParameters, referenceUpdater)
            return EClockingBlock(
                `class`.location,
                `class`.bodyStartLocation,
                `class`.bodyEndLocation,
                `class`.name,
                `class`.type,
                ports,
                eventValueParameterIndex
            )
        }

        private fun interpretPorts(
            valueParameters: List<EKtValueParameter>?,
            referenceUpdater: ReferenceUpdater
        ): List<EPort> {
            return valueParameters
                ?.mapNotNull { interpretPort(it, referenceUpdater) }
                ?: listOf()
        }

        private fun interpretPort(valueParameter: EKtValueParameter, referenceUpdater: ReferenceUpdater): EPort? {
            val portType = when {
                valueParameter.hasAnnotation(Annotations.IN) -> {
                    if (!valueParameter.isMutable)
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, valueParameter.name)
                    PortType.INPUT
                }
                valueParameter.hasAnnotation(Annotations.OUT) -> {
                    if (!valueParameter.isMutable)
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, valueParameter.name)
                    PortType.OUTPUT
                }
                else -> {
                    when {
                        valueParameter.type.isSubtype(Core.Vk.C_ModuleInterface.toType()) -> PortType.MODULE_INTERFACE
                        valueParameter.type.isSubtype(Core.Vk.C_ModulePort.toType()) -> PortType.MODULE_PORT
                        valueParameter.type.isSubtype(Core.Vk.C_ClockingBlock.toType()) -> PortType.CLOCKING_BLOCK
                        else -> null
                    }
                }
            }
            return if (portType != null) {
                val port = EPort(valueParameter.location, valueParameter.name, valueParameter.type, portType)
                referenceUpdater.update(valueParameter, port)
                port
            } else {
                Messages.PORT_NO_DIRECTIONALITY.on(valueParameter, valueParameter.name)
                null
            }
        }
    }
}
