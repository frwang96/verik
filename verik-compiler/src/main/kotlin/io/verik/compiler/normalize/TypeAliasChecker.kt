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

package io.verik.compiler.normalize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val typeAliasVisitor = TypeAliasVisitor(projectStage)
        projectContext.project.accept(typeAliasVisitor)
    }

    private class TypeAliasVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val typeMap = HashMap<Int, ArrayList<Type>>()

        private fun addTypeRecursive(type: Type, element: EElement) {
            val hashCode = System.identityHashCode(type)
            val types = typeMap[hashCode]
            if (types != null) {
                for (typeListType in types) {
                    if (type === typeListType) {
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
            type.arguments.forEach { addTypeRecursive(it, element) }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addTypeRecursive(typedElement.type, typedElement)
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            super.visitAbstractClass(abstractClass)
            addTypeRecursive(abstractClass.superType, abstractClass)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach {
                addTypeRecursive(it, callExpression)
            }
        }
    }
}
