/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks for the illegal redeclarations of names.
 */
object NameRedeclarationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.regularNonRootPackages.forEach { it.accept(NameRedeclarationCheckerVisitor) }
        projectContext.project.regularRootPackage.accept(NameRedeclarationCheckerVisitor)
    }

    class DeclarationSet {

        private val declarations = ArrayList<EDeclaration>()
        private val names = HashSet<String>()
        private val duplicateNames = HashSet<String>()

        fun add(declaration: EDeclaration) {
            declarations.add(declaration)
            if (declaration.name in names)
                duplicateNames.add(declaration.name)
            else
                names.add(declaration.name)
        }

        fun checkDuplicates() {
            declarations.forEach {
                if (it.name in duplicateNames) {
                    Messages.NAME_REDECLARATION.on(it, it.name)
                }
            }
        }
    }

    private object NameRedeclarationCheckerVisitor : TreeVisitor() {

        override fun visitPackage(pkg: EPackage) {
            super.visitPackage(pkg)
            val declarationSet = DeclarationSet()
            pkg.files.forEach { file ->
                file.declarations.forEach { declarationSet.add(it) }
            }
            declarationSet.checkDuplicates()
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            super.visitAbstractContainerClass(abstractContainerClass)
            val declarationSet = DeclarationSet()
            abstractContainerClass.declarations.forEach { declarationSet.add(it) }
            declarationSet.checkDuplicates()
        }
    }
}
