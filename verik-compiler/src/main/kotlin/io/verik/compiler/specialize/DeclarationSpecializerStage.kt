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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.copy.CopierDeclarationIndexerVisitor
import io.verik.compiler.copy.CopyContext
import io.verik.compiler.copy.DeclarationBinding
import io.verik.compiler.copy.ElementCopier
import io.verik.compiler.copy.TypeParameterContext
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object DeclarationSpecializerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val entryPoints = getEntryPoints(projectContext)
        val declarationQueue = ArrayDeque(entryPoints.map { DeclarationBinding(it, TypeParameterContext.EMPTY) })
        val declarationSpecializerVisitor = DeclarationSpecializerVisitor(declarationQueue)
        val copyContext = CopyContext()
        val copierDeclarationIndexerVisitor = CopierDeclarationIndexerVisitor(copyContext)
        while (declarationQueue.isNotEmpty()) {
            val declarationBinding = declarationQueue.pop()
            if (!copyContext.contains(declarationBinding)) {
                declarationBinding.declaration.accept(declarationSpecializerVisitor)
                declarationBinding.declaration.accept(copierDeclarationIndexerVisitor)
            }
        }

        projectContext.project.files().forEach { file ->
            val declarations = file.declarations.mapNotNull {
                if (copyContext.contains(DeclarationBinding(it, TypeParameterContext.EMPTY))) {
                    ElementCopier.copy(it, copyContext)
                } else null
            }
            declarations.forEach { it.parent = file }
            file.declarations = ArrayList(declarations)
        }
    }

    private fun getEntryPoints(projectContext: ProjectContext): ArrayList<EDeclaration> {
        val entryPoints = ArrayList<EDeclaration>()
        if (projectContext.config.enableDeadCodeElimination) {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it is Annotated && it.hasAnnotation(Annotations.TOP)) {
                        if (it is TypeParameterized && it.typeParameters.isNotEmpty()) {
                            Messages.TYPE_PARAMETERS_ON_TOP.on(it)
                        } else {
                            entryPoints.push(it)
                        }
                    }
                }
            }
            if (entryPoints.isEmpty())
                Messages.NO_TOP_DECLARATIONS.on(projectContext.project)
        } else {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it !is TypeParameterized || it.typeParameters.isEmpty())
                        entryPoints.push(it)
                }
            }
        }
        return entryPoints
    }

    private class DeclarationSpecializerVisitor(
        private val declarationQueue: ArrayDeque<DeclarationBinding>
    ) : TreeVisitor() {

        private fun addReference(reference: Declaration) {
            if (reference is EDeclaration && reference.parent is EFile)
                declarationQueue.push(DeclarationBinding(reference, TypeParameterContext.EMPTY))
        }

        private fun addReference(type: Type) {
            type.arguments.forEach { addReference(it) }
            addReference(type.reference)
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addReference(typedElement.type)
            if (typedElement is EKtCallExpression) {
                typedElement.typeArguments.forEach {
                    addReference(it)
                }
            }
            if (typedElement is Reference) {
                addReference(typedElement.reference)
            }
        }
    }
}
