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
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ObjectCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ObjectCheckerVisitor)
    }

    private object ObjectCheckerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            when {
                cls.type.isSubtype(Core.Vk.C_Struct) -> {
                    if (cls.isObject)
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Struct", cls.name)
                }
                cls.type.isSubtype(Core.Vk.C_Module) -> {
                    val isSynthesisTop = cls.hasAnnotationEntry(AnnotationEntries.SYNTHESIS_TOP)
                    val isSimulationTop = cls.hasAnnotationEntry(AnnotationEntries.SIMULATION_TOP)
                    if (cls.isObject) {
                        if (isSynthesisTop)
                            Messages.EXPECTED_NOT_OBJECT.on(cls, "Synthesis top", cls.name)
                        if (!isSynthesisTop && !isSimulationTop)
                            Messages.EXPECTED_NOT_OBJECT.on(cls, "Module", cls.name)
                    } else {
                        if (isSimulationTop)
                            Messages.EXPECTED_OBJECT.on(cls, "Simulation top", cls.name)
                    }
                }
                cls.type.isSubtype(Core.Vk.C_ModuleInterface) -> {
                    if (cls.isObject)
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Module interface", cls.name)
                }
                cls.type.isSubtype(Core.Vk.C_ModulePort) -> {
                    if (cls.isObject)
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Module port", cls.name)
                }
                cls.type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                    if (cls.isObject)
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Clocking block", cls.name)
                }
            }
        }
    }
}
