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

package io.verik.compiler.check.mid

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

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            when {
                `class`.type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    `class`.primaryConstructor?.valueParameters?.forEach {
                        if (it.name != "event") { checkPort(it) }
                    }
                }
                `class`.type.isSubtype(Core.Vk.C_Component) ->
                    `class`.primaryConstructor?.valueParameters?.forEach { checkPort(it) }
            }
        }

        private fun checkPort(valueParameter: EKtValueParameter) {
            when {
                valueParameter.hasAnnotationEntry(AnnotationEntries.IN) -> {
                    if (!valueParameter.isMutable)
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, "Input", valueParameter.name)
                }
                valueParameter.hasAnnotationEntry(AnnotationEntries.OUT) -> {
                    if (!valueParameter.isMutable)
                        Messages.PORT_NOT_MUTABLE.on(valueParameter, "Output", valueParameter.name)
                }
                valueParameter.type.isSubtype(Core.Vk.C_ModuleInterface) -> {
                    if (valueParameter.isMutable)
                        Messages.PORT_MUTABLE.on(valueParameter, "Module interface", valueParameter.name)
                }
                valueParameter.type.isSubtype(Core.Vk.C_ModulePort) -> {
                    if (valueParameter.isMutable)
                        Messages.PORT_MUTABLE.on(valueParameter, "Module", valueParameter.name)
                }
                valueParameter.type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    if (valueParameter.isMutable)
                        Messages.PORT_MUTABLE.on(valueParameter, "Clocking block", valueParameter.name)
                }
                else -> {
                    if (valueParameter.isMutable)
                        Messages.PORT_NO_DIRECTIONALITY.on(valueParameter, valueParameter.name)
                }
            }
        }
    }
}
