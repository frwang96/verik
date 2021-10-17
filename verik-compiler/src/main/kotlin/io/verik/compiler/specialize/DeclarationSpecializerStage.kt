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

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import org.jetbrains.kotlin.backend.common.pop

object DeclarationSpecializerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val entryPoints = EntryPointUtil.getEntryPoints(projectContext)
        val declarationBindingQueue = ArrayDeque(
            entryPoints.map { DeclarationBinding(it, TypeParameterContext.EMPTY) }
        )

        val specializerContext = SpecializerContext(projectContext.config.enableDeadCodeElimination)
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
                val typeParameterContexts = specializerContext.matchTypeParameterContexts(
                    declaration,
                    TypeParameterContext.EMPTY
                )
                typeParameterContexts.map {
                    specializerContext.typeParameterContext = it
                    specializerContext.specialize(declaration)
                }
            }
            declarations.forEach { it.parent = file }
            file.declarations = ArrayList(declarations)
        }
    }
}
