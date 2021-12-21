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

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object DeadDeclarationEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        if (projectContext.config.enableDeadCodeElimination) {
            val declarationQueue = ArrayDeque(getEntryPoints(projectContext.project))
            val declarationSet = HashSet<EDeclaration>()
            val deadDeclarationIndexerVisitor = DeadDeclarationIndexerVisitor(declarationQueue)

            while (declarationQueue.isNotEmpty()) {
                val declaration = declarationQueue.pop()
                if (declaration !in declarationSet) {
                    declarationSet.add(declaration)
                    declaration.accept(deadDeclarationIndexerVisitor)
                }
            }

            val deadDeclarationEliminatorVisitor = DeadDeclarationEliminatorVisitor(declarationSet)
            projectContext.project.accept(deadDeclarationEliminatorVisitor)
        }
    }

    private fun getEntryPoints(project: EProject): List<EDeclaration> {
        val entryPoints = ArrayList<EDeclaration>()
        project.files().forEach { file ->
            file.declarations.forEach {
                if (it is EModule && it.isTop())
                    entryPoints.add(it)
            }
        }
        return entryPoints
    }

    private class DeadDeclarationIndexerVisitor(
        private val declarationQueue: ArrayDeque<EDeclaration>
    ) : TreeVisitor() {

        private fun addType(type: Type) {
            type.arguments.forEach { addType(it) }
            val reference = type.reference
            if (reference is EDeclaration)
                declarationQueue.push(reference)
        }

        private fun addDeclaration(declaration: EDeclaration) {
            declarationQueue.push(declaration)
            val parent = declaration.parent
            if (parent is EDeclaration)
                addDeclaration(parent)
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addType(typedElement.type)
            if (typedElement is Reference) {
                val reference = typedElement.reference
                if (reference is EDeclaration)
                    addDeclaration(reference)
            }
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            abstractContainerClass.declarations.forEach { declarationQueue.push(it) }
        }

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            abstractContainerComponent.declarations.forEach { declarationQueue.push(it) }
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

        private fun filter(declarations: List<EDeclaration>): ArrayList<EDeclaration> {
            val filteredDeclarations = declarations.filter { it in declarationSet }
            return ArrayList(filteredDeclarations)
        }
    }
}
