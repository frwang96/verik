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

package io.verik.compiler.check.normalize

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeAliasChecker : NormalizationStage {

    override fun process(projectContext: ProjectContext, projectStage: ProjectStage) {
        val typeAliasVisitor = TypeAliasVisitor(projectStage)
        projectContext.project.accept(typeAliasVisitor)
    }

    private class TypeAliasVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val typeMap = HashMap<Int, ArrayList<Type>>()

        private fun addTypeRecursive(type: Type, element: EElement) {
            val hashCode = System.identityHashCode(type)
            val typeList = typeMap[hashCode]
            if (typeList != null) {
                for (typeListType in typeList) {
                    if (type === typeListType) {
                        Messages.NORMALIZATION_ERROR.on(
                            element,
                            projectStage,
                            "Unexpected type aliasing: $type in $element"
                        )
                    }
                }
                typeList.add(type)
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

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments.forEach {
                addTypeRecursive(it, callExpression)
            }
        }
    }
}
