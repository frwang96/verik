/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.normalize

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.sv.EStructLiteralExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object DanglingReferenceChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
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
                is EKtFunction -> {
                    typedElement.overriddenFunction?.let { checkReference(it, typedElement) }
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
