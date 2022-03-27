/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.normalize

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ElementAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val elementAliasVisitor = ElementAliasVisitor(projectStage)
        projectContext.project.accept(elementAliasVisitor)
    }

    private class ElementAliasVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val elementSet = HashSet<EElement>()

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element in elementSet)
                Messages.NORMALIZATION_ERROR.on(element, projectStage, "Unexpected element aliasing: $element")
            elementSet.add(element)
        }
    }
}
