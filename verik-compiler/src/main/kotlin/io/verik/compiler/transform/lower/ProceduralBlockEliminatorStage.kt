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

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAbstractProceduralBlock
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ProceduralBlockEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ProceduralBlockEliminatorVisitor)
    }

    private object ProceduralBlockEliminatorVisitor : TreeVisitor() {

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            val declarations = abstractContainerComponent.declarations.filter {
                it !is EAbstractProceduralBlock || it.body.statements.isNotEmpty()
            }
            abstractContainerComponent.declarations = ArrayList(declarations)
        }
    }
}
