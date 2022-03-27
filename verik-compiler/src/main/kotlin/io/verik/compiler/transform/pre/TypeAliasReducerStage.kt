/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeAliasReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeAliasReducerVisitor)
    }

    private object TypeAliasReducerVisitor : TreeVisitor() {

        private fun reduce(type: Type, element: EElement) {
            var reference = type.reference
            while (reference is ETypeAlias) {
                val expectedSize = reference.typeParameters.size
                val actualSize = type.arguments.size
                if (expectedSize != actualSize) {
                    Messages.INTERNAL_ERROR.on(
                        element,
                        "Type alias expected $expectedSize arguments but found $actualSize"
                    )
                }
                val typeParameterBindings = reference.typeParameters
                    .zip(type.arguments)
                    .map { (typeParameter, type) ->
                        TypeParameterBinding(typeParameter, type)
                    }
                val arguments = reference.type.arguments.map { substitute(it, typeParameterBindings, element) }
                type.reference = reference.type.reference
                type.arguments = ArrayList(arguments)
                reference = type.reference
            }
            type.arguments.forEach { reduce(it, element) }
        }

        private fun substitute(
            type: Type,
            typeParameterBindings: List<TypeParameterBinding>,
            element: EElement
        ): Type {
            val reference = type.reference
            return if (reference is ETypeParameter) {
                val typeParameterBinding = typeParameterBindings.find { it.typeParameter == reference }
                    ?: Messages.INTERNAL_ERROR.on(element, "Could not bind type parameter: ${reference.name}")
                typeParameterBinding.type.copy()
            } else {
                val arguments = type.arguments.map { substitute(it, typeParameterBindings, element) }
                reference.toType(arguments)
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            reduce(typedElement.type, typedElement)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach { reduce(it, callExpression) }
        }
    }

    private data class TypeParameterBinding(val typeParameter: ETypeParameter, val type: Type)
}
