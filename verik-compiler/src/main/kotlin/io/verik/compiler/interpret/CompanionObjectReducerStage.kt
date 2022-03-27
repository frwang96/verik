/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CompanionObjectReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CompanionObjectReducerVisitor)
    }

    private object CompanionObjectReducerVisitor : TreeVisitor() {

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            val declarations = ArrayList<EDeclaration>()
            cls.declarations.forEach { declaration ->
                if (declaration is ECompanionObject) {
                    declaration.declarations.forEach { it.parent = cls }
                    declarations.addAll(declaration.declarations)
                } else {
                    declarations.add(declaration)
                }
            }
            cls.declarations = declarations
        }
    }
}
