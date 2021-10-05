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

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

object ComponentInterpreter {

    fun interpretComponent(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater): Boolean {
        val basicClassType = basicClass.toType()
        return when {
            basicClassType.isSubtype(Core.Vk.C_Module.toType()) -> {
                val ports = interpretPorts(basicClass.primaryConstructor?.valueParameters)
                val isTop = basicClass.hasAnnotation(Annotations.TOP)
                val module = EModule(
                    basicClass.location,
                    basicClass.name,
                    basicClass.supertype,
                    basicClass.typeParameters,
                    ports,
                    basicClass.declarations,
                    isTop
                )
                referenceUpdater.replace(basicClass, module)
                basicClass.primaryConstructor?.let { referenceUpdater.update(it, module) }
                true
            }
            basicClassType.isSubtype(Core.Vk.C_ModuleInterface.toType()) -> {
                val ports = interpretPorts(basicClass.primaryConstructor?.valueParameters)
                val moduleInterface = EModuleInterface(
                    basicClass.location,
                    basicClass.name,
                    basicClass.supertype,
                    basicClass.typeParameters,
                    ports,
                    basicClass.declarations
                )
                referenceUpdater.replace(basicClass, moduleInterface)
                basicClass.primaryConstructor?.let { referenceUpdater.update(it, moduleInterface) }
                true
            }
            basicClassType.isSubtype(Core.Vk.C_ModulePort.toType()) -> {
                val ports = interpretPorts(basicClass.primaryConstructor?.valueParameters)
                val modulePort = EModulePort(
                    basicClass.location,
                    basicClass.name,
                    basicClass.supertype,
                    basicClass.typeParameters,
                    ports,
                    null
                )
                referenceUpdater.replace(basicClass, modulePort)
                basicClass.primaryConstructor?.let { referenceUpdater.update(it, modulePort) }
                true
            }
            basicClassType.isSubtype(Core.Vk.C_ClockingBlock.toType()) -> {
                val valueParameters = basicClass.primaryConstructor?.valueParameters
                val eventValueParameter = valueParameters?.find { it.name == "event" }
                if (eventValueParameter != null) {
                    val eventValueParameterIndex = valueParameters.indexOf(eventValueParameter)
                    valueParameters.remove(eventValueParameter)
                    val ports = interpretPorts(valueParameters)
                    val clockingBlock = EClockingBlock(
                        basicClass.location,
                        basicClass.name,
                        basicClass.supertype,
                        basicClass.typeParameters,
                        ports,
                        eventValueParameterIndex
                    )
                    referenceUpdater.replace(basicClass, clockingBlock)
                    basicClass.primaryConstructor?.let { referenceUpdater.update(it, clockingBlock) }
                } else {
                    Messages.INTERNAL_ERROR.on(basicClass, "Could not identify clocking block event value parameter")
                }
                true
            }
            else -> false
        }
    }

    private fun interpretPorts(valueParameters: List<EKtValueParameter>?): List<EPort> {
        return valueParameters
            ?.mapNotNull { interpretPort(it) }
            ?: listOf()
    }

    private fun interpretPort(valueParameter: EKtValueParameter): EPort? {
        val portType = when {
            valueParameter.hasAnnotation(Annotations.IN) -> PortType.INPUT
            valueParameter.hasAnnotation(Annotations.OUT) -> PortType.OUTPUT
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
            EPort(valueParameter.location, valueParameter.name, valueParameter.type, portType)
        } else {
            Messages.PORT_NO_DIRECTIONALITY.on(valueParameter, valueParameter.name)
            null
        }
    }
}
