/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.normalize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Normalization checker that checks that types are not aliased between elements.
 */
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

        override fun visitScopeExpression(scopeExpression: EScopeExpression) {
            super.visitScopeExpression(scopeExpression)
            addTypeRecursive(scopeExpression.scope, scopeExpression)
        }
    }
}
