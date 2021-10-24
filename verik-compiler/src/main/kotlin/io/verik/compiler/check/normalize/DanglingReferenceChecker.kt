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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object DanglingReferenceChecker : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val danglingReferenceIndexerVisitor = DanglingReferenceIndexerVisitor()
        projectContext.project.accept(danglingReferenceIndexerVisitor)
        val declarations = danglingReferenceIndexerVisitor.declarations
        val danglingReferenceCheckerVisitor = DanglingReferenceCheckerVisitor(declarations)
        projectContext.project.accept(danglingReferenceCheckerVisitor)
    }

    private class DanglingReferenceIndexerVisitor : TreeVisitor() {

        val declarations = HashSet<EDeclaration>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    private class DanglingReferenceCheckerVisitor(private val declarations: HashSet<EDeclaration>) : TreeVisitor() {

        private fun checkReference(type: Type, element: EElement) {
            type.arguments.forEach { checkReference(it, element) }
            checkReference(type.reference, element)
        }

        private fun checkReference(reference: Declaration, element: EElement) {
            if (reference is EDeclaration && reference !in declarations)
                Messages.INTERNAL_ERROR.on(element, "Dangling reference to ${reference.name} in $element")
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkReference(typedElement.type, typedElement)
            if (typedElement is Reference) {
                checkReference(typedElement.reference, typedElement)
            }
            when (typedElement) {
                is EKtBasicClass -> {
                    typedElement.superTypeCallEntry?.let { checkReference(it.reference, typedElement) }
                }
                is EKtConstructor -> {
                    typedElement.superTypeCallEntry?.let { checkReference(it.reference, typedElement) }
                }
                is EAbstractComponentInstantiation -> {
                    typedElement.portInstantiations.forEach { checkReference(it.reference, typedElement) }
                }
                is EKtCallExpression -> {
                    typedElement.typeArguments.forEach { checkReference(it, typedElement) }
                }
                is EStructLiteralExpression -> {
                    typedElement.entries.forEach { checkReference(it.reference, typedElement) }
                }
            }
        }
    }
}
