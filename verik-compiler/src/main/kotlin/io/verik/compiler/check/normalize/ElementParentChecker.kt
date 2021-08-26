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

package io.verik.compiler.check.normalize

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object ElementParentChecker : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val elementParentVisitor = ElementParentVisitor()
        projectContext.project.accept(elementParentVisitor)
    }

    class ElementParentVisitor : TreeVisitor() {

        private val parentStack = ArrayDeque<EElement>()

        override fun visitElement(element: EElement) {
            val parent = element.parentNotNull()
            val expectedParent = parentStack.last()
            if (parent != expectedParent) {
                Messages.INTERNAL_ERROR.on(
                    element,
                    "Mismatch in parent element of $element: Expected $expectedParent but was $parent"
                )
            }
            parentStack.push(element)
            super.visitElement(element)
            parentStack.pop()
        }

        override fun visitProject(project: EProject) {
            if (project.parent != null)
                Messages.INTERNAL_ERROR.on(project, "Parent element should be null")
            parentStack.push(project)
            project.acceptChildren(this)
            parentStack.pop()
        }
    }
}
