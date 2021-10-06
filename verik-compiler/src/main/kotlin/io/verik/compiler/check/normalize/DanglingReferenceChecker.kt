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

import io.verik.compiler.ast.element.common.EAbstractCallExpression
import io.verik.compiler.ast.element.common.EAbstractReferenceExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.interfaces.Declaration
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

    class DanglingReferenceIndexerVisitor : TreeVisitor() {

        val declarations = HashSet<EDeclaration>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    class DanglingReferenceCheckerVisitor(private val declarations: HashSet<EDeclaration>) : TreeVisitor() {

        private fun checkReference(reference: Declaration, element: EElement) {
            if (reference is EDeclaration && reference !in declarations)
                Messages.INTERNAL_ERROR.on(element, "Dangling reference to ${reference.name} in $element")
        }

        override fun visitAbstractComponentInstantiation(
            abstractComponentInstantiation: EAbstractComponentInstantiation
        ) {
            super.visitAbstractComponentInstantiation(abstractComponentInstantiation)
            abstractComponentInstantiation.portInstantiations.forEach {
                checkReference(it.reference, abstractComponentInstantiation)
            }
        }

        override fun visitAbstractReferenceExpression(abstractReferenceExpression: EAbstractReferenceExpression) {
            super.visitAbstractReferenceExpression(abstractReferenceExpression)
            checkReference(abstractReferenceExpression.reference, abstractReferenceExpression)
        }

        override fun visitAbstractCallExpression(abstractCallExpression: EAbstractCallExpression) {
            super.visitAbstractCallExpression(abstractCallExpression)
            checkReference(abstractCallExpression.reference, abstractCallExpression)
        }

        override fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
            super.visitStructLiteralExpression(structLiteralExpression)
            structLiteralExpression.entries.forEach {
                checkReference(it.reference, structLiteralExpression)
            }
        }
    }
}
