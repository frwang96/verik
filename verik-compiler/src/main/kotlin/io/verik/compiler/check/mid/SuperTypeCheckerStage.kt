/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object SuperTypeCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SuperTypeCheckerVisitor)
    }

    private object SuperTypeCheckerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            if (cls.superType.reference == Core.Kt.C_Any) {
                Messages.SUPER_TYPE_MISSING.on(cls, cls.name)
            }
        }
    }
}
