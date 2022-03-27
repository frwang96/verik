/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.ast.element.declaration.ECompanionObject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ESvAbstractFunction
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object CompanionObjectInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CompanionObjectInterpreterVisitor)
    }

    object CompanionObjectInterpreterVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val isParameterized = cls.typeParameters.isNotEmpty()
            val declarations = ArrayList<EDeclaration>()
            val companionObjectDeclarations = ArrayList<EDeclaration>()
            cls.declarations.forEach {
                if (isCompanionObjectDeclaration(it)) {
                    if (isParameterized && it is EProperty) {
                        Messages.PARAMETERIZED_STATIC_PROPERTY.on(it, it.name)
                    } else {
                        companionObjectDeclarations.add(it)
                    }
                } else {
                    declarations.add(it)
                }
            }
            if (companionObjectDeclarations.isNotEmpty()) {
                val companionObject = ECompanionObject(
                    cls.location,
                    companionObjectDeclarations
                )
                companionObject.parent = cls
                declarations.add(companionObject)
            }
            cls.declarations = declarations
        }

        private fun isCompanionObjectDeclaration(declaration: EDeclaration): Boolean {
            return when (declaration) {
                is ESvConstructor -> false
                is ESvAbstractFunction -> declaration.isStatic
                is EProperty -> declaration.isStatic
                else -> false
            }
        }
    }
}
