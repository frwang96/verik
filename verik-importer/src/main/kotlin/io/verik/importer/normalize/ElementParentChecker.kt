/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.normalize

import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.kt.element.KtProject
import io.verik.importer.ast.sv.element.common.SvCompilationUnit
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.common.KtTreeVisitor
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object ElementParentChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val svElementParentVisitor = SvElementParentVisitor(projectStage)
        projectContext.compilationUnit.accept(svElementParentVisitor)
        val ktElementParentVisitor = KtElementParentVisitor(projectStage)
        projectContext.project.accept(ktElementParentVisitor)
    }

    private class SvElementParentVisitor(
        private val projectStage: ProjectStage
    ) : SvTreeVisitor() {

        private val parentStack = ArrayDeque<SvElement>()

        @Suppress("DuplicatedCode")
        override fun visitElement(element: SvElement) {
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

        override fun visitCompilationUnit(compilationUnit: SvCompilationUnit) {
            if (compilationUnit.parent != null) {
                Messages.NORMALIZATION_ERROR.on(compilationUnit, projectStage, "Parent element should be null")
            }
            parentStack.addLast(compilationUnit)
            compilationUnit.acceptChildren(this)
            parentStack.removeLast()
        }
    }

    private class KtElementParentVisitor(
        private val projectStage: ProjectStage
    ) : KtTreeVisitor() {

        private val parentStack = ArrayDeque<KtElement>()

        @Suppress("DuplicatedCode")
        override fun visitElement(element: KtElement) {
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

        override fun visitProject(project: KtProject) {
            if (project.parent != null) {
                Messages.NORMALIZATION_ERROR.on(project, projectStage, "Parent element should be null")
            }
            parentStack.addLast(project)
            project.acceptChildren(this)
            parentStack.removeLast()
        }
    }
}
