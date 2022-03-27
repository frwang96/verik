/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.normalize

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object ElementParentChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val elementParentVisitor = ElementParentVisitor(projectStage)
        projectContext.project.accept(elementParentVisitor)
    }

    private class ElementParentVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val parentStack = ArrayDeque<EElement>()

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
            parentStack.push(element)
            super.visitElement(element)
            parentStack.pop()
        }

        override fun visitProject(project: EProject) {
            if (project.parent != null)
                Messages.NORMALIZATION_ERROR.on(project, projectStage, "Parent element should be null")
            parentStack.push(project)
            project.acceptChildren(this)
            parentStack.pop()
        }
    }
}
