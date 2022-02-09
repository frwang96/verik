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

package io.verik.compiler.test

import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.StageSequencer
import io.verik.compiler.main.StageType
import org.intellij.lang.annotations.Language

abstract class CoreDeclarationTest : BaseTest() {

    fun driveCoreDeclarationTest(
        coreDeclarations: List<CoreDeclaration>,
        @Language("kotlin") content: String,
        expected: String
    ) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, StageType.PRE_TRANSFORM)
        checkCoreDeclarations(coreDeclarations, projectContext)
        stageSequence.processUntil(projectContext, StageType.POST_CHECK)
        projectContext.project.accept(PropertyEliminatorVisitor)
        stageSequence.processAll(projectContext)

        val nonRootPackageTextFiles = projectContext.outputContext.nonRootPackageTextFiles
        val rootPackageTextFiles = projectContext.outputContext.rootPackageTextFiles
        when {
            nonRootPackageTextFiles.size + rootPackageTextFiles.size > 1 ->
                throw IllegalArgumentException("Multiple source files generated")
            nonRootPackageTextFiles.size + rootPackageTextFiles.size == 0 ->
                throw IllegalArgumentException("No source files generated")
            nonRootPackageTextFiles.size == 1 ->
                assertOutputTextEquals(expected, nonRootPackageTextFiles[0])
            rootPackageTextFiles.size == 1 ->
                assertOutputTextEquals(expected, rootPackageTextFiles[0])
        }
    }

    private fun checkCoreDeclarations(coreDeclarations: List<CoreDeclaration>, projectContext: ProjectContext) {
        val coreDeclarationIndexerVisitor = CoreDeclarationIndexerVisitor()
        projectContext.project.accept(coreDeclarationIndexerVisitor)
        coreDeclarations.forEach {
            assert(it in coreDeclarationIndexerVisitor.coreDeclarations) {
                "Core declaration not found: ${it.signature}"
            }
        }
    }

    private class CoreDeclarationIndexerVisitor : TreeVisitor() {

        val coreDeclarations = HashSet<CoreDeclaration>()

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            val reference = receiverExpression.reference
            if (reference is CoreDeclaration)
                coreDeclarations.add(reference)
        }
    }

    private object PropertyEliminatorVisitor : TreeVisitor() {

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = eliminateProperties(file.declarations)
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            super.visitAbstractContainerClass(abstractContainerClass)
            abstractContainerClass.declarations = eliminateProperties(abstractContainerClass.declarations)
        }

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            abstractContainerComponent.declarations = eliminateProperties(abstractContainerComponent.declarations)
        }

        private fun eliminateProperties(declarations: ArrayList<EDeclaration>): ArrayList<EDeclaration> {
            return ArrayList(declarations.filter { it !is EAbstractProperty })
        }
    }
}
