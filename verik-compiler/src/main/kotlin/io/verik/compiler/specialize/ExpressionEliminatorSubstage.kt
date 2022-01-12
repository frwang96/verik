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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ExpressionEvaluator
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.transform.upper.ConstantPropagator

object ExpressionEliminatorSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        declaration.accept(ExpressionEliminatorVisitor)
    }

    private object ExpressionEliminatorVisitor : TreeVisitor() {

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            val condition = ConstantPropagator.expand(ExpressionCopier.deepCopy(ifExpression.condition))
            val typeParameterSubstitutorVisitor = TypeParameterSubstitutorVisitor(ifExpression)
            condition.accept(typeParameterSubstitutorVisitor)
            if (!typeParameterSubstitutorVisitor.isAbleToBind)
                return

            val blockExpression = EBlockExpression.wrap(condition)
            condition.accept(ExpressionEvaluatorVisitor)
            val evaluatedCondition = blockExpression.statements[0]
            when (ConstantNormalizer.parseBooleanOrNull(evaluatedCondition)) {
                true -> {
                    val expression = getExpressionFromBlockExpression(ifExpression.thenExpression, ifExpression)
                    ifExpression.replace(expression)
                }
                false -> {
                    val expression = getExpressionFromBlockExpression(ifExpression.elseExpression, ifExpression)
                    ifExpression.replace(expression)
                }
            }
        }

        private fun getExpressionFromBlockExpression(
            blockExpression: EBlockExpression?,
            parentExpression: EExpression
        ): EExpression {
            return when {
                blockExpression == null -> EBlockExpression.empty(parentExpression.location)
                blockExpression.statements.size == 1 -> blockExpression.statements[0]
                else -> blockExpression
            }
        }
    }

    private class TypeParameterSubstitutorVisitor(expression: EExpression) : TreeVisitor() {

        private val typeParameters: List<ETypeParameter>
        var isAbleToBind = true

        init {
            var parent: EElement = expression
            while (parent.parent != null) {
                parent = parent.parent!!
            }
            typeParameters = if (parent is TypeParameterized) {
                parent.typeParameters
            } else listOf()
        }

        private fun substitute(type: Type, element: EElement): Type {
            val reference = type.reference
            // TODO bind type parameter by identity and not name
            return if (reference is ETypeParameter) {
                val typeParameter = typeParameters.find { it.name == reference.name }
                if (typeParameter != null) {
                    typeParameter.type.copy()
                } else {
                    isAbleToBind = false
                    type
                }
            } else {
                val arguments = type.arguments.map { substitute(it, element) }
                reference.toType(arguments)
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            typedElement.type = substitute(typedElement.type, typedElement)
        }

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            `class`.superType = substitute(`class`.superType, `class`)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { substitute(it, callExpression) }
            )
        }
    }

    private object ExpressionEvaluatorVisitor : TreeVisitor() {

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            val evaluatedExpression = ExpressionEvaluator.evaluate(expression)
            if (evaluatedExpression != null)
                expression.replace(evaluatedExpression)
        }
    }
}
