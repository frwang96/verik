/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.EWidthCastExpression
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

/**
 * Core extension functions from Boolean.
 */
object CoreVkBoolean : CoreScope(CorePackage.VK) {

    val F_toUbit = object : TransformableCoreFunctionDeclaration(parent, "toUbit", "fun toUbit()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return if (value == 1) {
                callExpression.receiver!!
            } else {
                EWidthCastExpression(
                    callExpression.location,
                    callExpression.type,
                    callExpression.receiver!!,
                    value
                )
            }
        }
    }

    val F_toSbit = object : TransformableCoreFunctionDeclaration(parent, "toSbit", "fun toSbit()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_toUbit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return if (value == 1) {
                callExpressionSigned
            } else {
                EWidthCastExpression(
                    callExpression.location,
                    callExpression.type,
                    callExpressionSigned,
                    value
                )
            }
        }
    }
}
