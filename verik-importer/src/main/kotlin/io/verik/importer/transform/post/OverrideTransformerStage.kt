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

package io.verik.importer.transform.post

import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object OverrideTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(OverrideTransformerVisitor)
    }

    object OverrideTransformerVisitor : TreeVisitor() {

        private fun getSuperClass(descriptor: EDescriptor): EKtClass? {
            return when (val declaration = descriptor.type.reference) {
                is ETypeAlias -> getSuperClass(declaration.descriptor)
                is EKtClass -> declaration
                else -> null
            }
        }

        private fun isOverride(declaration: EDeclaration, superClass: EKtClass): Boolean {
            val superSuperClass = getSuperClass(superClass.superDescriptor)
            if (superSuperClass != null && isOverride(declaration, superSuperClass)) {
                return true
            }
            superClass.declarations.forEach {
                if (it.name == declaration.name) {
                    when {
                        it is EProperty && declaration is EProperty -> return true
                        it is EKtFunction && declaration is EKtFunction -> return true
                    }
                }
            }
            return false
        }

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val superClass = getSuperClass(`class`.superDescriptor)
            if (superClass != null) {
                val declarations = ArrayList<EDeclaration>()
                `class`.declarations.forEach { declaration ->
                    when (declaration) {
                        is EProperty -> {
                            if (!isOverride(declaration, superClass)) declarations.add(declaration)
                        }
                        is EKtFunction -> {
                            declaration.isOverride = isOverride(declaration, superClass)
                            declaration.valueParameters.forEach { it.hasDefault = false }
                            declarations.add(declaration)
                        }
                        else -> declarations.add(declaration)
                    }
                }
                `class`.declarations = ArrayList(declarations)
            }
        }
    }
}
