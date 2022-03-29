/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.EPort
import io.verik.compiler.ast.property.PortKind
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that interprets SystemVerilog modules, interfaces, module ports, and clocking blocks from Kotlin classes if
 * applicable.
 */
object ComponentInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val componentInterpreterVisitor = ComponentInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(componentInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ComponentInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val classType = cls.type
            when {
                classType.isSubtype(Core.Vk.C_Module) -> {
                    val module = interpretModule(cls)
                    referenceUpdater.replace(cls, module)
                    cls.primaryConstructor?.let { referenceUpdater.update(it, module) }
                }
                classType.isSubtype(Core.Vk.C_ModuleInterface) -> {
                    val moduleInterface = interpretModuleInterface(cls)
                    referenceUpdater.replace(cls, moduleInterface)
                    cls.primaryConstructor?.let { referenceUpdater.update(it, moduleInterface) }
                }
                classType.isSubtype(Core.Vk.C_ModulePort) -> {
                    val modulePort = interpretModulePort(cls)
                    referenceUpdater.replace(cls, modulePort)
                    cls.primaryConstructor?.let { referenceUpdater.update(it, modulePort) }
                }
                classType.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    val clockingBlock = interpretClockingBlock(cls)
                    referenceUpdater.replace(cls, clockingBlock)
                    cls.primaryConstructor?.let { referenceUpdater.update(it, clockingBlock) }
                }
            }
        }

        private fun interpretModule(cls: EKtClass): EModule {
            val ports = interpretPorts(cls.primaryConstructor?.valueParameters, referenceUpdater)
            return EModule(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                ports,
                cls.declarations
            )
        }

        private fun interpretModuleInterface(cls: EKtClass): EModuleInterface {
            val ports = interpretPorts(cls.primaryConstructor?.valueParameters, referenceUpdater)
            return EModuleInterface(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                ports,
                cls.declarations
            )
        }

        private fun interpretModulePort(cls: EKtClass): EModulePort {
            val ports = interpretPorts(cls.primaryConstructor?.valueParameters, referenceUpdater)
            return EModulePort(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                ports,
                null
            )
        }

        private fun interpretClockingBlock(cls: EKtClass): EClockingBlock {
            val valueParameters = cls.primaryConstructor?.valueParameters
            val eventValueParameter = valueParameters?.find { it.name == "event" }
                ?: Messages.INTERNAL_ERROR.on(cls, "Could not identify clocking block event value parameter")
            val eventValueParameterIndex = valueParameters.indexOf(eventValueParameter)
            valueParameters.remove(eventValueParameter)

            val ports = interpretPorts(valueParameters, referenceUpdater)
            return EClockingBlock(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                ports,
                eventValueParameterIndex
            )
        }

        private fun interpretPorts(
            valueParameters: List<EKtValueParameter>?,
            referenceUpdater: ReferenceUpdater
        ): List<EPort> {
            return valueParameters
                ?.map { interpretPort(it, referenceUpdater) }
                ?: listOf()
        }

        private fun interpretPort(valueParameter: EKtValueParameter, referenceUpdater: ReferenceUpdater): EPort {
            val kind = when {
                valueParameter.hasAnnotationEntry(AnnotationEntries.IN) -> PortKind.INPUT
                valueParameter.hasAnnotationEntry(AnnotationEntries.OUT) -> PortKind.OUTPUT
                valueParameter.hasAnnotationEntry(AnnotationEntries.WIRE) -> PortKind.INOUT
                else -> {
                    when {
                        valueParameter.type.isSubtype(Core.Vk.C_ModuleInterface) -> PortKind.MODULE_INTERFACE
                        valueParameter.type.isSubtype(Core.Vk.C_ModulePort) -> PortKind.MODULE_PORT
                        valueParameter.type.isSubtype(Core.Vk.C_ClockingBlock) -> PortKind.CLOCKING_BLOCK
                        else -> PortKind.INPUT
                    }
                }
            }
            val port = EPort(
                valueParameter.location,
                valueParameter.name,
                valueParameter.type,
                valueParameter.annotationEntries,
                kind
            )
            referenceUpdater.update(valueParameter, port)
            return port
        }
    }
}
