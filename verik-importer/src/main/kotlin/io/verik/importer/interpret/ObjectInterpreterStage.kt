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

package io.verik.importer.interpret

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.ESvAbstractFunction
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object ObjectInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ObjectInterpreterVisitor)
    }

    object ObjectInterpreterVisitor : TreeVisitor() {

        // TODO create companion object
        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val declarations = `class`.declarations.filter {
                when (it) {
                    is ESvConstructor -> true
                    is ESvAbstractFunction -> !it.isStatic
                    else -> true
                }
            }
            `class`.declarations = ArrayList(declarations)
        }
    }
}
