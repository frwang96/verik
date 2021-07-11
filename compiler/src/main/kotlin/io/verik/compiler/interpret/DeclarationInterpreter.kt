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

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.DeclarationReplacer
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object DeclarationInterpreter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val declarationReplacer = DeclarationReplacer(projectContext)
        val declarationVisitor = DeclarationVisitor(declarationReplacer)
        projectContext.files.forEach {
            it.accept(declarationVisitor)
        }
        declarationReplacer.updateReferences()
    }

    class DeclarationVisitor(private val declarationReplacer: DeclarationReplacer) : TreeVisitor() {

        override fun visitFile(file: EFile) {
            file.declarations.forEach { interpretDeclaration(it) }
            super.visitFile(file)
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            abstractClass.declarations.forEach { interpretDeclaration(it) }
            super.visitAbstractClass(abstractClass)
        }

        private fun interpretDeclaration(declaration: EDeclaration) {
            when (declaration) {
                is EKtBasicClass -> declarationReplacer.replace(declaration, ClassInterpreter.interpret(declaration))
                is EKtFunction ->
                    FunctionInterpreter.interpret(declaration)?.let { declarationReplacer.replace(declaration, it) }
                is EKtProperty -> declarationReplacer.replace(declaration, PropertyInterpreter.interpret(declaration))
            }
        }
    }
}