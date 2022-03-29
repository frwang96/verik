/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAbstractProceduralBlock
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that eliminates procedural blocks that are empty.
 */
object ProceduralBlockEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ProceduralBlockEliminatorVisitor)
    }

    private object ProceduralBlockEliminatorVisitor : TreeVisitor() {

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            val declarations = abstractContainerComponent.declarations.filter {
                it !is EAbstractProceduralBlock || it.body.statements.isNotEmpty()
            }
            abstractContainerComponent.declarations = ArrayList(declarations)
        }
    }
}
