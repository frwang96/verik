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
import io.verik.importer.ast.element.declaration.ETypeParameter
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

        private fun isOverride(property: EProperty, superClass: EKtClass): Boolean {
            val superSuperClass = getSuperClass(superClass.superDescriptor)
            if (superSuperClass != null && isOverride(property, superSuperClass)) {
                return true
            }
            superClass.declarations.forEach {
                if (it is EProperty && it.name == property.name) return true
            }
            return false
        }

        private fun isOverride(function: EKtFunction, superClass: EKtClass): Boolean {
            val superSuperClass = getSuperClass(superClass.superDescriptor)
            if (superSuperClass != null && isOverride(function, superSuperClass)) {
                return true
            }
            for (superClassFunction in superClass.declarations) {
                if (superClassFunction is EKtFunction && superClassFunction.name == function.name) {
                    if (superClassFunction.valueParameters.size != function.valueParameters.size) continue
                    val isOverride = superClassFunction.valueParameters.zip(function.valueParameters).all {
                        val superClassValueParameterReference = it.first.descriptor.type.reference
                        val valueParameterReference = it.second.descriptor.type.reference
                        superClassValueParameterReference is ETypeParameter ||
                            valueParameterReference is ETypeParameter ||
                            superClassValueParameterReference == valueParameterReference
                    }
                    if (isOverride) return true
                }
            }
            return false
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val superClass = getSuperClass(cls.superDescriptor)
            if (superClass != null) {
                val declarations = ArrayList<EDeclaration>()
                cls.declarations.forEach { declaration ->
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
                cls.declarations = ArrayList(declarations)
            }
        }
    }
}
