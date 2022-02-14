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
            if (cls.isObject) {
                when {
                    cls.type.isSubtype(Core.Vk.C_Struct) -> {
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Struct", cls.name)
                    }
                    cls.type.isSubtype(Core.Vk.C_ModuleInterface) -> {
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Module interface", cls.name)
                    }
                    cls.type.isSubtype(Core.Vk.C_ModulePort) -> {
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Module port", cls.name)
                    }
                    cls.type.isSubtype(Core.Vk.C_ClockingBlock) -> {
                        Messages.EXPECTED_NOT_OBJECT.on(cls, "Clocking block", cls.name)
                    }
                }
            }
        }
    }
}
