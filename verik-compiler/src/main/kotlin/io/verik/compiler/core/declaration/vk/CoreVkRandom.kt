/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind
import io.verik.compiler.target.common.Target

object CoreVkRandom : CoreScope(CorePackage.VK) {

    val F_random = BasicCoreFunctionDeclaration(parent, "random", "fun random()", Target.F_random)

    val F_random_Int = object : TransformableCoreFunctionDeclaration(parent, "random", "fun random(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val expression = CoreTransformUtil.callExpressionDecrement(callExpression.valueArguments[0])
            return ECallExpression(
                callExpression.location,
                callExpression.type,
                Target.F_urandom_range,
                null,
                false,
                arrayListOf(expression),
                ArrayList()
            )
        }
    }

    val F_random_Int_Int = object : TransformableCoreFunctionDeclaration(parent, "random", "fun random(Int, Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val expression = CoreTransformUtil.callExpressionDecrement(callExpression.valueArguments[1])
            return ECallExpression(
                callExpression.location,
                callExpression.type,
                Target.F_urandom_range,
                null,
                false,
                arrayListOf(callExpression.valueArguments[0], expression),
                ArrayList()
            )
        }
    }

    val F_randomBoolean = BasicCoreFunctionDeclaration(
        parent,
        "randomBoolean",
        "fun randomBoolean()",
        Target.F_urandom
    )

    val F_randomUbit = object : BasicCoreFunctionDeclaration(
        parent,
        "randomUbit",
        "fun randomUbit()",
        Target.F_urandom
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }
    }
}
