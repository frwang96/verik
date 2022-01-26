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

package io.verik.compiler.normalize

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object DanglingReferenceChecker : NormalizationStage {

    override fun process(projectContext: ProjectContext, projectStage: ProjectStage) {
        val danglingReferenceIndexerVisitor = DanglingReferenceIndexerVisitor()
        projectContext.project.accept(danglingReferenceIndexerVisitor)
        val declarations = danglingReferenceIndexerVisitor.declarations
        val specializeContext = projectContext.specializeContext
        if (specializeContext != null) {
            declarations.addAll(specializeContext.getOriginalDeclarations())
        }

        val danglingReferenceCheckerVisitor = DanglingReferenceCheckerVisitor(declarations, projectStage)
        projectContext.project.accept(danglingReferenceCheckerVisitor)
    }

    private class DanglingReferenceIndexerVisitor : TreeVisitor() {

        val declarations = HashSet<EDeclaration>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    private class DanglingReferenceCheckerVisitor(
        private val declarations: HashSet<EDeclaration>,
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private fun checkReference(type: Type, element: EElement) {
            type.arguments.forEach { checkReference(it, element) }
            checkReference(type.reference, element)
        }

        private fun checkReference(reference: Declaration, element: EElement) {
            if (reference is EDeclaration && reference !in declarations)
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Dangling reference to ${reference.name} in $element"
                )
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkReference(typedElement.type, typedElement)
            if (typedElement is EAbstractClass) {
                checkReference(typedElement.superType, typedElement)
            }
            when (typedElement) {
                is EEnum -> {
                    typedElement.enumEntries.forEach { checkReference(it, typedElement) }
                }
                is EAbstractComponentInstantiation -> {
                    typedElement.portInstantiations.forEach { checkReference(it.port, typedElement) }
                }
                is EReferenceExpression -> {
                    checkReference(typedElement.reference, typedElement)
                }
                is ECallExpression -> {
                    checkReference(typedElement.reference, typedElement)
                    typedElement.typeArguments.forEach { checkReference(it, typedElement) }
                }
                is EStructLiteralExpression -> {
                    typedElement.entries.forEach { checkReference(it.reference, typedElement) }
                }
            }
        }
    }
}
