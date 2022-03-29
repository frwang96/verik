/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext

/**
 * Utility class for indexing entry points for the specializer. If dead code elimination is not enabled, all eligible
 * entry points are indexed.
 */
object EntryPointIndexer {

    fun getEntryPoints(projectContext: ProjectContext): List<EDeclaration> {
        val entryPointIndexerVisitor = EntryPointIndexerVisitor(
            projectContext.config.enableDeadCodeElimination,
            projectContext.config.entryPoints
        )
        projectContext.project.regularPackages().forEach {
            it.accept(entryPointIndexerVisitor)
        }
        if (projectContext.config.enableDeadCodeElimination) {
            projectContext.report.entryPoints = entryPointIndexerVisitor.entryPoints.mapNotNull {
                if (isTopLevelInjectedProperty(it)) null
                else it.getQualifiedName()
            }
        }
        return entryPointIndexerVisitor.entryPoints
    }

    private fun isTopLevelInjectedProperty(declaration: EDeclaration): Boolean {
        return declaration.hasAnnotationEntry(AnnotationEntries.INJ) &&
            declaration is EProperty &&
            declaration.parent is EFile
    }

    private class EntryPointIndexerVisitor(
        private val enableDeadCodeElimination: Boolean,
        private val entryPointNames: List<String>
    ) : TreeVisitor() {

        val entryPoints = ArrayList<EDeclaration>()

        private fun addDeclaration(declaration: EDeclaration) {
            if (isEntryPoint(declaration)) {
                entryPoints.add(declaration)
            }
        }

        private fun isEntryPoint(declaration: EDeclaration): Boolean {
            if (isTopLevelInjectedProperty(declaration)) return true
            return if (enableDeadCodeElimination) {
                if (declaration.hasAnnotationEntry(AnnotationEntries.ENTRY)) {
                    when {
                        declaration is TypeParameterized && declaration.typeParameters.isNotEmpty() -> false
                        entryPointNames.isEmpty() || declaration.getQualifiedName() in entryPointNames -> true
                        else -> false
                    }
                } else false
            } else when {
                declaration is TypeParameterized && declaration.typeParameters.isNotEmpty() -> false
                declaration is ETypeAlias -> false
                else -> true
            }
        }

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations.forEach { addDeclaration(it) }
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.declarations
                .filterIsInstance<EKtClass>()
                .forEach { addDeclaration(it) }
        }

        override fun visitCompanionObject(companionObject: ECompanionObject) {
            super.visitCompanionObject(companionObject)
            companionObject.declarations.forEach { addDeclaration(it) }
        }
    }
}
