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

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object TypeAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val typeIndexerVisitor = TypeIndexerVisitor(projectStage)
        projectContext.compilationUnit.accept(typeIndexerVisitor)
    }

    private class TypeIndexerVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val typeMap = HashMap<Int, ArrayList<Type>>()

        fun addType(type: Type, element: EElement) {
            val hashCode = System.identityHashCode(type)
            val types = typeMap[hashCode]
            if (types != null) {
                types.forEach {
                    if (type === it) {
                        Messages.NORMALIZATION_ERROR.on(
                            element,
                            projectStage,
                            "Unexpected type aliasing: $type in $element"
                        )
                    }
                }
                types.add(type)
            } else {
                typeMap[hashCode] = arrayListOf(type)
            }
            type.arguments.forEach { addType(it, element) }
        }

        override fun visitDescriptor(descriptor: EDescriptor) {
            super.visitDescriptor(descriptor)
            addType(descriptor.type, descriptor)
        }
    }
}
