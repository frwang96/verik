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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object DeclarationSpecializerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val entryPoints = getEntryPoints(projectContext)
        val declarationBindingQueue = ArrayDeque(
            entryPoints.map { DeclarationBinding(it, TypeParameterContext.EMPTY) }
        )

        val specializerContext = SpecializerContext()
        val declarationSpecializeIndexerVisitor = DeclarationSpecializeIndexerVisitor(
            declarationBindingQueue,
            specializerContext
        )
        while (declarationBindingQueue.isNotEmpty()) {
            val declarationBinding = declarationBindingQueue.pop()
            if (!specializerContext.contains(declarationBinding)) {
                specializerContext.typeParameterContext = declarationBinding.typeParameterContext
                declarationBinding.declaration.accept(declarationSpecializeIndexerVisitor)
            }
        }

        projectContext.project.files().forEach { file ->
            val declarations = file.declarations.flatMap { declaration ->
                val typeParameterContexts = specializerContext.getTypeParameterContexts(declaration)
                typeParameterContexts.map {
                    specializerContext.typeParameterContext = it
                    specializerContext.specialize(declaration)
                }
            }
            declarations.forEach { it.parent = file }
            file.declarations = ArrayList(declarations)
        }
    }

    private fun getEntryPoints(projectContext: ProjectContext): ArrayList<EDeclaration> {
        val entryPoints = ArrayList<EDeclaration>()
        if (projectContext.config.enableDeadCodeElimination) {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it is Annotated && it.hasAnnotation(Annotations.TOP)) {
                        if (it is TypeParameterized && it.typeParameters.isNotEmpty()) {
                            Messages.TYPE_PARAMETERS_ON_TOP.on(it)
                        } else {
                            entryPoints.push(it)
                        }
                    }
                }
            }
            if (entryPoints.isEmpty())
                Messages.NO_TOP_DECLARATIONS.on(projectContext.project)
        } else {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it !is TypeParameterized || it.typeParameters.isEmpty())
                        entryPoints.push(it)
                }
            }
        }
        return entryPoints
    }
}
