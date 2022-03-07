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

import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ClassDeclarationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ClassDeclarationCheckerVisitor)
    }

    private object ClassDeclarationCheckerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            when {
                cls.isEnum -> {
                    cls.declarations.forEach {
                        if (it !is EEnumEntry) Messages.ILLEGAL_CLASS_DECLARATION.on(it, "enum")
                    }
                }
                cls.type.isSubtype(Core.Vk.C_Struct) -> {
                    cls.declarations.forEach {
                        Messages.ILLEGAL_CLASS_DECLARATION.on(it, "struct")
                    }
                }
                cls.type.isSubtype(Core.Vk.C_Class) -> {
                    cls.declarations.forEach {
                        if (it is EKtClass &&
                            (it.type.isSubtype(Core.Vk.C_CoverGroup) || it.type.isSubtype(Core.Vk.C_Component))
                        ) {
                            Messages.ILLEGAL_CLASS_DECLARATION.on(it, "class")
                        }
                    }
                }
            }
        }
    }
}
