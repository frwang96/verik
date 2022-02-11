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
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
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
        return if (projectContext.config.enableDeadCodeElimination) {
            val entryPoints = ArrayList<EDeclaration>()
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it is EKtClass && it.typeParameters.isEmpty()) {
                        val isSynthesisTop = it.hasAnnotationEntry(AnnotationEntries.SYNTHESIS_TOP)
                        val isSimulationTop = it.hasAnnotationEntry(AnnotationEntries.SIMULATION_TOP)
                        if (isSynthesisTop || isSimulationTop) {
                            val entryPointNames = projectContext.config.entryPoints
                            if (entryPointNames.isEmpty() || it.name in entryPointNames) {
                                entryPoints.add(it)
                            }
                        }
                    }
                }
            }
            entryPoints
        } else {
            val entryPointIndexerVisitor = EntryPointIndexerVisitor()
            projectContext.project.accept(entryPointIndexerVisitor)
            entryPointIndexerVisitor.entryPoints
        }
    }

    private class EntryPointIndexerVisitor : TreeVisitor() {

        val entryPoints = ArrayList<EDeclaration>()

        private fun addDeclaration(declaration: EDeclaration) {
            if (declaration !is TypeParameterized) {
                entryPoints.add(declaration)
            } else if (declaration !is ETypeAlias && declaration.typeParameters.isEmpty()) {
                entryPoints.add(declaration)
            }
        }

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations.forEach { addDeclaration(it) }
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.declarations
                .filterIsInstance<EAbstractClass>()
                .forEach { addDeclaration(it) }
        }

        override fun visitCompanionObject(companionObject: ECompanionObject) {
            super.visitCompanionObject(companionObject)
            companionObject.declarations.forEach { addDeclaration(it) }
        }
    }
}
