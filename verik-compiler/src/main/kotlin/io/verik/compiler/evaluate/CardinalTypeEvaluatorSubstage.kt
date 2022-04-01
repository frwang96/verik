/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.specialize.SpecializerSubstage
import io.verik.compiler.specialize.TypeParameterBinding

/**
 * Specializer substage that evaluates cardinal types.
 */
object CardinalTypeEvaluatorSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        declaration.accept(CardinalTypeEvaluatorVisitor)
    }

    private object CardinalTypeEvaluatorVisitor : TreeVisitor() {

        fun evaluate(type: Type, element: EElement): Type {
            val reference = type.reference
            val arguments = type.arguments.map { evaluate(it, element) }
            return if (reference is CoreCardinalFunctionDeclaration) {
                val argumentValues = arguments.map { it.asCardinalValue(element) }
                val value = evaluateCardinalFunction(reference, argumentValues, element)
                Cardinal.of(value).toType()
            } else {
                reference.toType(arguments)
            }
        }

        private fun evaluateCardinalFunction(
            reference: CoreCardinalFunctionDeclaration,
            argumentValues: List<Int>,
            element: EElement
        ): Int {
            return when (reference) {
                Core.Vk.T_TRUE -> 1
                Core.Vk.T_FALSE -> 0
                Core.Vk.T_NOT -> {
                    if (argumentValues[0] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                    if (argumentValues[0] != 0) 0 else 1
                }
                Core.Vk.T_AND -> {
                    if (argumentValues[0] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                    if (argumentValues[1] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[1]).toType())
                    if ((argumentValues[0] != 0) && (argumentValues[1] != 0)) 1 else 0
                }
                Core.Vk.T_OR -> {
                    if (argumentValues[0] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                    if (argumentValues[1] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[1]).toType())
                    if ((argumentValues[0] != 0) || (argumentValues[1] != 0)) 1 else 0
                }
                Core.Vk.T_IF -> {
                    if (argumentValues[0] !in 0..1)
                        Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                    if (argumentValues[0] != 0) {
                        argumentValues[1]
                    } else argumentValues[2]
                }
                Core.Vk.T_ADD -> argumentValues[0] + argumentValues[1]
                Core.Vk.T_SUB -> argumentValues[0] - argumentValues[1]
                Core.Vk.T_MUL -> argumentValues[0] * argumentValues[1]
                Core.Vk.T_DIV -> argumentValues[0] / argumentValues[1]
                Core.Vk.T_MAX -> Integer.max(argumentValues[0], argumentValues[1])
                Core.Vk.T_MIN -> Integer.min(argumentValues[0], argumentValues[1])
                Core.Vk.T_OF -> argumentValues[0]
                Core.Vk.T_INC -> argumentValues[0] + 1
                Core.Vk.T_DEC -> argumentValues[0] - 1
                Core.Vk.T_LOG -> {
                    if (argumentValues[0] <= 0) 0 else (32 - (argumentValues[0] - 1).countLeadingZeroBits())
                }
                Core.Vk.T_WIDTH -> {
                    if (argumentValues[0] < 0) 0 else (32 - argumentValues[0].countLeadingZeroBits())
                }
                Core.Vk.T_EXP -> {
                    if (argumentValues[0] >= 31)
                        Messages.CARDINAL_OUT_OF_RANGE.on(element)
                    1 shl argumentValues[0]
                }
                else -> {
                    Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: ${reference.name}")
                }
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            typedElement.type = evaluate(typedElement.type, typedElement)
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.superType = evaluate(cls.superType, cls)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { evaluate(it, callExpression) }
            )
        }
    }
}
