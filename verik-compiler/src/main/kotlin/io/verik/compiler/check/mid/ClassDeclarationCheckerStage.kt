/*
 * SPDX-License-Identifier: Apache-2.0
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
