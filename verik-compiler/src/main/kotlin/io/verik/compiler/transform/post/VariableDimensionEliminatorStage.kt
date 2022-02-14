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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.common.ResizableDeclarationContainer
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ETypeDefinition
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object VariableDimensionEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val variableDimensionEliminatorVisitor = VariableDimensionEliminatorVisitor()
        projectContext.project.accept(variableDimensionEliminatorVisitor)
        variableDimensionEliminatorVisitor.typeDefinitionEntries.forEach {
            val parent = it.function.parent
            if (parent is ResizableDeclarationContainer) {
                parent.insertChildBefore(it.function, it.typeDefinition)
            } else {
                Messages.INTERNAL_ERROR.on(it.typeDefinition, "Count not insert ${it.typeDefinition} into $parent")
            }
        }
    }

    private class VariableDimensionEliminatorVisitor : TreeVisitor() {

        val typeDefinitionEntries = ArrayList<TypeDefinitionEntry>()

        override fun visitSvFunction(function: ESvFunction) {
            super.visitSvFunction(function)
            if (function.type.reference == Core.Vk.C_Unpacked) {
                val typeDefinition = ETypeDefinition(
                    function.location,
                    "<tmp>",
                    function.type.copy()
                )
                function.type.reference = typeDefinition
                typeDefinitionEntries.add(TypeDefinitionEntry(typeDefinition, function))
            }
        }
    }

    data class TypeDefinitionEntry(val typeDefinition: ETypeDefinition, val function: ESvFunction)
}