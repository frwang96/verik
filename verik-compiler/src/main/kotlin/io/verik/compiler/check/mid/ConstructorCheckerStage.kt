/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ConstructorCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ConstructorCheckerVisitor)
    }

    private object ConstructorCheckerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            var constructorCount = 0
            if (cls.primaryConstructor != null) constructorCount++
            cls.declarations.forEach {
                if (it is ESecondaryConstructor) constructorCount++
            }
            if (constructorCount > 1) {
                cls.primaryConstructor?.let { Messages.MULTIPLE_CONSTRUCTORS.on(it) }
                cls.declarations.forEach {
                    if (it is ESecondaryConstructor) {
                        Messages.MULTIPLE_CONSTRUCTORS.on(it)
                    }
                }
            }
        }
    }
}
