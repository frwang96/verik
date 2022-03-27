/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.normalize

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object ElementParentChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val elementParentVisitor = ElementParentVisitor(projectStage)
        projectContext.project.accept(elementParentVisitor)
    }

    private class ElementParentVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val parentStack = ArrayDeque<EElement>()

        @Suppress("DuplicatedCode")
        override fun visitElement(element: EElement) {
            val parent = element.parentNotNull()
            val expectedParent = parentStack.last()
            if (parent != expectedParent) {
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Mismatch in parent element of $element: Expected $expectedParent but was $parent"
                )
            }
            parentStack.addLast(element)
            super.visitElement(element)
            parentStack.removeLast()
        }

        override fun visitProject(project: EProject) {
            if (project.parent != null) {
                Messages.NORMALIZATION_ERROR.on(project, projectStage, "Parent element should be null")
            }
            parentStack.addLast(project)
            project.acceptChildren(this)
            parentStack.removeLast()
        }
    }
}
