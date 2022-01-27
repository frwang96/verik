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

package io.verik.importer.check

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
                is EEnum -> true
                is ETypeAlias -> true
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
