/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

/**
 * Core declarations from Packed.
 */
object CoreVkPacked : CoreScope(Core.Vk.C_Packed) {

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            val value = referenceExpression.receiver!!.type.arguments[0].asCardinalValue(referenceExpression)
            return EConstantExpression(
                referenceExpression.location,
                referenceExpression.type,
                value.toString()
            )
        }
    }

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_OUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofElement(callExpression.receiver!!, 1)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_get_Ubit = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_OUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofElement(callExpression.receiver!!, 1)
                ),
                TypeConstraint(
                    TypeConstraintKind.LOG_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_get_Int.transform(callExpression)
        }
    }

    val F_set_Int_E = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, E)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[1]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 1)
                ),
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val receiver = ESvArrayAccessExpression(
                callExpression.location,
                callExpression.valueArguments[1].type.copy(),
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
            return EKtBinaryExpression(
                callExpression.location,
                callExpression.type,
                receiver,
                callExpression.valueArguments[1],
                KtBinaryOperatorKind.EQ
            )
        }
    }

    val F_set_Ubit_E = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(E)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[1]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 1)
                ),
                TypeConstraint(
                    TypeConstraintKind.LOG_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_set_Int_E.transform(callExpression)
        }
    }
}
