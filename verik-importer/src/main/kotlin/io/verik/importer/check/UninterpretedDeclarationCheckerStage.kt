/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.check

import io.verik.importer.ast.element.declaration.ECompanionObject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFile
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtPackage
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object UninterpretedDeclarationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UninterpretedDeclarationCheckerVisitor)
    }

    private object UninterpretedDeclarationCheckerVisitor : TreeVisitor() {

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            if (!isInterpreted(declaration)) {
                Messages.INTERNAL_ERROR.on(
                    declaration,
                    "Declaration has not been interpreted as Kotlin: ${declaration.name}"
                )
            }
        }

        private fun isInterpreted(declaration: EDeclaration): Boolean {
            return when (declaration) {
                is EKtPackage -> true
                is EKtFile -> true
                is EKtClass -> true
                is ECompanionObject -> true
                is EEnum -> true
                is ETypeAlias -> true
                is ETypeParameter -> true
                is EKtFunction -> true
                is EKtConstructor -> true
                is EProperty -> true
                is EKtValueParameter -> true
                is EEnumEntry -> true
                else -> false
            }
        }
    }
}
