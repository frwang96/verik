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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CompanionObjectReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CompanionObjectReducerVisitor)
    }

    private object CompanionObjectReducerVisitor : TreeVisitor() {

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            val declarations = ArrayList<EDeclaration>()
            cls.declarations.forEach { declaration ->
                if (declaration is ECompanionObject) {
                    declaration.declarations.forEach { it.parent = cls }
                    declarations.addAll(declaration.declarations)
                } else {
                    declarations.add(declaration)
                }
            }
            cls.declarations = declarations
        }
    }
}
