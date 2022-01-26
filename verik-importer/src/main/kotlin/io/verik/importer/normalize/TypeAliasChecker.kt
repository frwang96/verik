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

package io.verik.importer.normalize

import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.common.KtTreeVisitor
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.common.Type
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

object TypeAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val typeSet = TypeSet(projectStage)
        val svTypeIndexerVisitor = SvTypeIndexerVisitor(typeSet)
        projectContext.compilationUnit.accept(svTypeIndexerVisitor)
        val ktTypeIndexerVisitor = KtTypeIndexerVisitor(typeSet)
        projectContext.project.accept(ktTypeIndexerVisitor)
    }

    private class TypeSet(
        private val projectStage: ProjectStage
    ) {

        private val typeMap = HashMap<Int, ArrayList<Type>>()

        fun addType(type: Type, element: Any) {
            val hashCode = System.identityHashCode(type)
            val types = typeMap[hashCode]
            if (types != null) {
                checkType(type, types, element)
                types.add(type)
            } else {
                typeMap[hashCode] = arrayListOf(type)
            }
            type.arguments.forEach { addType(it, element) }
        }

        private fun checkType(type: Type, types: List<Type>, element: Any) {
            types.forEach {
                if (type === it) {
                    when (element) {
                        is SvElement -> {
                            Messages.NORMALIZATION_ERROR.on(
                                element,
                                projectStage,
                                "Unexpected type aliasing: $type in $element"
                            )
                        }
                        is KtElement -> {
                            Messages.NORMALIZATION_ERROR.on(
                                element,
                                projectStage,
                                "Unexpected type aliasing: $type in $element"
                            )
                        }
                        else -> {
                            Messages.INTERNAL_ERROR.on(
                                SourceLocation.NULL,
                                "Unexpected element type: ${element::class.simpleName}"
                            )
                        }
                    }
                }
            }
        }
    }

    private class SvTypeIndexerVisitor(
        private val typeSet: TypeSet
    ) : SvTreeVisitor() {

        override fun visitDescriptor(descriptor: SvDescriptor) {
            super.visitDescriptor(descriptor)
            typeSet.addType(descriptor.type, descriptor)
        }
    }

    private class KtTypeIndexerVisitor(
        private val typeSet: TypeSet
    ) : KtTreeVisitor() {

        override fun visitElement(element: KtElement) {
            super.visitElement(element)
            if (element is KtDeclaration) {
                typeSet.addType(element.type, element)
            }
            if (element is KtClass) {
                typeSet.addType(element.superType, element)
            }
        }
    }
}
