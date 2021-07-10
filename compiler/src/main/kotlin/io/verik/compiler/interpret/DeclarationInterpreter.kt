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

import io.verik.compiler.ast.element.common.CAbstractClass
import io.verik.compiler.ast.element.common.CDeclaration
import io.verik.compiler.ast.element.common.CFile
import io.verik.compiler.ast.element.kt.KBasicClass
import io.verik.compiler.ast.element.kt.KFunction
import io.verik.compiler.ast.element.kt.KProperty
import io.verik.compiler.common.DeclarationReplacer
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object DeclarationInterpreter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val declarationReplacer = DeclarationReplacer(projectContext)
        val declarationVisitor = DeclarationVisitor(declarationReplacer)
        projectContext.verikFiles.forEach {
            it.accept(declarationVisitor)
        }
        declarationReplacer.updateReferences()
    }

    class DeclarationVisitor(private val declarationReplacer: DeclarationReplacer) : TreeVisitor() {

        override fun visitCFile(file: CFile) {
            file.declarations.forEach { interpretDeclaration(it) }
            super.visitCFile(file)
        }

        override fun visitCAbstractClass(abstractClass: CAbstractClass) {
            abstractClass.declarations.forEach { interpretDeclaration(it) }
            super.visitCAbstractClass(abstractClass)
        }

        private fun interpretDeclaration(declaration: CDeclaration) {
            when (declaration) {
                is KBasicClass -> declarationReplacer.replace(declaration, ClassInterpreter.interpret(declaration))
                is KFunction -> declarationReplacer.replace(declaration, FunctionInterpreter.interpret(declaration))
                is KProperty -> declarationReplacer.replace(declaration, PropertyInterpreter.interpret(declaration))
            }
        }
    }
}