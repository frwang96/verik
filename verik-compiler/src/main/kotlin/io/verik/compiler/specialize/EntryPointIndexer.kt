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

package io.verik.compiler.specialize

import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext

object EntryPointIndexer {

    fun getEntryPoints(projectContext: ProjectContext): List<EDeclaration> {
        val entryPointIndexerVisitor = EntryPointIndexerVisitor(
            projectContext.config.enableDeadCodeElimination,
            projectContext.config.entryPoints
        )
        projectContext.project.accept(entryPointIndexerVisitor)
        return entryPointIndexerVisitor.entryPoints
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
            return if (enableDeadCodeElimination) {
                if (declaration.hasAnnotationEntry(AnnotationEntries.ENTRY_POINT)) {
                    when {
                        declaration is TypeParameterized && declaration.typeParameters.isNotEmpty() -> false
                        entryPointNames.isEmpty() || declaration.name in entryPointNames -> true
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
