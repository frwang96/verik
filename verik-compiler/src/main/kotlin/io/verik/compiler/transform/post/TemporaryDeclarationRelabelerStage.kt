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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object TemporaryDeclarationRelabelerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val temporaryDeclarationRelabelerVisitor = TemporaryDeclarationRelabelerVisitor()
        projectContext.project.files().forEach { file ->
            file.declarations.forEach {
                temporaryDeclarationRelabelerVisitor.resetIndex()
                it.accept(temporaryDeclarationRelabelerVisitor)
            }
        }
    }

    private class TemporaryDeclarationRelabelerVisitor : TreeVisitor() {

        private var index = 0

        fun resetIndex() {
            index = 0
        }

        override fun visitDeclaration(declaration: EDeclaration) {
            if (declaration.name == "<tmp>") {
                declaration.name = "_$$index"
                index++
            }
            super.visitDeclaration(declaration)
        }
    }
}
