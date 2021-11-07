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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EAbstractProceduralBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object DeadDeclarationEliminatorStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        if (projectContext.config.enableDeadCodeElimination) {
            val declarationQueue = ArrayDeque(getEntryPoints(projectContext.project))
            val declarationSet = HashSet<EDeclaration>()
            val deadDeclarationIndexerVisitor = DeadDeclarationIndexerVisitor(declarationQueue)

            while (declarationQueue.isNotEmpty()) {
                val declaration = declarationQueue.pop()
                if (declaration is EDeclaration && declaration !in declarationSet) {
                    declarationSet.add(declaration)
                    declaration.accept(deadDeclarationIndexerVisitor)
                }
            }

            val deadDeclarationEliminatorVisitor = DeadDeclarationEliminatorVisitor(declarationSet)
            projectContext.project.accept(deadDeclarationEliminatorVisitor)
        }
    }

    private fun getEntryPoints(project: EProject): List<Declaration> {
        val entryPoints = ArrayList<Declaration>()
        project.files().forEach { file ->
            file.declarations.forEach {
                if (it is EModule && it.isTop())
                    entryPoints.add(it)
            }
        }
        return entryPoints
    }

    private class DeadDeclarationIndexerVisitor(
        private val declarationQueue: ArrayDeque<Declaration>
    ) : TreeVisitor() {

        private fun addType(type: Type) {
            type.arguments.forEach { addType(it) }
            declarationQueue.push(type.reference)
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addType(typedElement.type)
            if (typedElement is Reference) {
                declarationQueue.push(typedElement.reference)
            }
        }

        override fun visitSvBasicClass(basicClass: ESvBasicClass) {}

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            abstractContainerComponent.declarations.forEach {
                when (it) {
                    is EAbstractProceduralBlock -> declarationQueue.push(it)
                    is EAbstractComponentInstantiation -> declarationQueue.push(it)
                }
            }
        }
    }

    private class DeadDeclarationEliminatorVisitor(
        private val declarationSet: HashSet<EDeclaration>
    ) : TreeVisitor() {

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = filter(file.declarations)
        }

        override fun visitSvBasicClass(basicClass: ESvBasicClass) {
            super.visitSvBasicClass(basicClass)
            basicClass.declarations = filter(basicClass.declarations)
        }

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            abstractContainerComponent.declarations = filter(abstractContainerComponent.declarations)
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            val filteredStatements = blockExpression.statements.filter {
                if (it is EPropertyStatement) it.property in declarationSet
                else true
            }
            blockExpression.statements = ArrayList(filteredStatements)
        }

        private fun filter(declarations: List<EDeclaration>): ArrayList<EDeclaration> {
            val filteredDeclarations = declarations.filter { it in declarationSet }
            return ArrayList(filteredDeclarations)
        }
    }
}
