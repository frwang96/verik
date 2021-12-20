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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EAbstractPackage
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object NameRedeclarationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(NameRedeclarationCheckerVisitor)
    }

    class DeclarationSet {

        private val declarations = ArrayList<EDeclaration>()
        private val names = HashSet<String>()
        private val duplicateNames = HashSet<String>()

        fun add(declaration: EDeclaration) {
            declarations.add(declaration)
            if (declaration.name in names)
                duplicateNames.add(declaration.name)
            else
                names.add(declaration.name)
        }

        fun checkDuplicates() {
            declarations.forEach {
                if (it.name in duplicateNames) {
                    Messages.NAME_REDECLARATION.on(it, it.name)
                }
            }
        }
    }

    private object NameRedeclarationCheckerVisitor : TreeVisitor() {

        override fun visitAbstractPackage(abstractPackage: EAbstractPackage) {
            super.visitAbstractPackage(abstractPackage)
            val declarationSet = DeclarationSet()
            abstractPackage.files.forEach { file ->
                file.declarations.forEach { declarationSet.add(it) }
            }
            declarationSet.checkDuplicates()
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            super.visitAbstractContainerClass(abstractContainerClass)
            val declarationSet = DeclarationSet()
            abstractContainerClass.declarations.forEach { declarationSet.add(it) }
            declarationSet.checkDuplicates()
        }
    }
}
