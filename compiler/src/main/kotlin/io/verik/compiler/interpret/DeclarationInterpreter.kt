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

package io.verik.compiler.interpret

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.*
import io.verik.compiler.common.DeclarationReplacer
import io.verik.compiler.main.ProjectContext

object DeclarationInterpreter {

    fun interpret(projectContext: ProjectContext) {
        val declarationReplacer = DeclarationReplacer(projectContext)
        val declarationVisitor = DeclarationVisitor(declarationReplacer)
        projectContext.vkFiles.forEach {
            it.accept(declarationVisitor)
        }
        declarationReplacer.updateReferences()
    }

    class DeclarationVisitor(private val declarationReplacer: DeclarationReplacer) : TreeVisitor() {

        override fun visitFile(file: VkFile) {
            file.declarations.forEach { interpretDeclaration(it) }
            super.visitFile(file)
        }

        override fun visitBaseClass(baseClass: VkBaseClass) {
            baseClass.declarations.forEach { interpretDeclaration(it) }
            super.visitBaseClass(baseClass)
        }

        private fun interpretDeclaration(declaration: VkDeclaration) {
            when (declaration) {
                is VkKtClass -> declarationReplacer.replace(declaration, ClassInterpreter.interpret(declaration))
                is VkKtFunction -> declarationReplacer.replace(declaration, FunctionInterpreter.interpret(declaration))
                is VkKtProperty -> declarationReplacer.replace(declaration, PropertyInterpreter.interpret(declaration))
            }
        }
    }
}