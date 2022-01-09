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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.backend.common.pop

object SpecializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarationBindings = ArrayList(getEntryPoints(projectContext))
        val specializeContext = SpecializeContext()
        while (declarationBindings.isNotEmpty()) {
            val declarationBinding = declarationBindings.pop()
            if (!specializeContext.contains(declarationBinding)) {
                declarationBindings.addAll(DeclarationSpecializer.specialize(declarationBinding, specializeContext))
            }
        }

        projectContext.project.files().forEach { file ->
            val declarations = file.declarations.flatMap { specializeContext.getSpecializedDeclarations(it) }
            declarations.forEach { it.parent = file }
            file.declarations = ArrayList(declarations)
        }
        projectContext.specializeContext = specializeContext
    }

    private fun getEntryPoints(projectContext: ProjectContext): List<DeclarationBinding> {
        val declarations = ArrayList<EDeclaration>()
        if (projectContext.config.enableDeadCodeElimination) {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it is EKtClass && it.typeParameters.isEmpty()) {
                        val isSynthesisTop = it.hasAnnotationEntry(AnnotationEntries.SYNTHESIS_TOP)
                        val isSimulationTop = it.hasAnnotationEntry(AnnotationEntries.SIMULATION_TOP)
                        if (isSynthesisTop || isSimulationTop) {
                            val entryPointNames = projectContext.config.entryPoints
                            if (entryPointNames.isEmpty() || it.name in entryPointNames) {
                                declarations.add(it)
                            }
                        }
                    }
                }
            }
        } else {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it !is TypeParameterized) {
                        declarations.add(it)
                    } else if (it !is ETypeAlias && it.typeParameters.isEmpty()) {
                        declarations.add(it)
                    }
                }
            }
        }
        return declarations.map { DeclarationBinding(it, listOf()) }
    }
}
