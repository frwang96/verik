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
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object PropertyOverrideTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PropertyOverrideTransformerVisitor)
    }

    object PropertyOverrideTransformerVisitor : TreeVisitor() {

        private fun isOverride(property: EProperty, superClass: EKtClass): Boolean {
            val superSuperClass = superClass.superDescriptor.type.reference
            if (superSuperClass is EKtClass && isOverride(property, superSuperClass)) {
                return true
            }
            superClass.declarations.forEach {
                if (it is EProperty && it.name == property.name) {
                    return true
                }
            }
            return false
        }

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val superClass = `class`.superDescriptor.type.reference
            if (superClass is EKtClass) {
                val declarations = ArrayList<EDeclaration>()
                `class`.declarations.forEach {
                    if (it !is EProperty || !isOverride(it, superClass)) {
                        declarations.add(it)
                    }
                }
                `class`.declarations = declarations
            }
        }
    }
}
