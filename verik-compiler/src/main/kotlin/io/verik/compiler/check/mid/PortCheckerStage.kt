/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object PortCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PortCheckerVisitor)
    }

    private object PortCheckerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            when {
                cls.type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    cls.primaryConstructor?.valueParameters?.forEach {
                        if (it.name != "event") { checkPort(it) }
                    }
                }
                cls.type.isSubtype(Core.Vk.C_Component) ->
                    cls.primaryConstructor?.valueParameters?.forEach { checkPort(it) }
            }
        }

        private fun checkPort(valueParameter: EKtValueParameter) {
            val type = unwrapType(valueParameter.type)
            when {
                valueParameter.hasAnnotationEntry(AnnotationEntries.IN) -> {}
                valueParameter.hasAnnotationEntry(AnnotationEntries.OUT) -> {
                    if (!valueParameter.isMutable) {
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, "Output", valueParameter.name)
                    }
                }
                valueParameter.hasAnnotationEntry(AnnotationEntries.WIRE) -> {
                    if (!valueParameter.isMutable) {
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, "Wire", valueParameter.name)
                    }
                }
                type.isSubtype(Core.Vk.C_ModuleInterface) -> {
                    if (valueParameter.isMutable) {
                        Messages.PORT_MUTABLE.on(valueParameter, "Module interface", valueParameter.name)
                    }
                }
                type.isSubtype(Core.Vk.C_ModulePort) -> {
                    if (valueParameter.isMutable) {
                        Messages.PORT_MUTABLE.on(valueParameter, "Module", valueParameter.name)
                    }
                }
                type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    if (valueParameter.isMutable) {
                        Messages.PORT_MUTABLE.on(valueParameter, "Clocking block", valueParameter.name)
                    }
                }
                else -> {
                    Messages.PORT_NO_DIRECTIONALITY.on(valueParameter, valueParameter.name)
                }
            }
        }

        private fun unwrapType(type: Type): Type {
            return if (type.reference == Core.Vk.C_Cluster) {
                type.arguments[1]
            } else type
        }
    }
}
