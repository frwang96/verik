/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

/**
 * Core declarations from String.
 */
object CoreKtString : CoreScope(Core.Kt.C_String) {

    val F_plus_Any = object : TransformableCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Int)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            val receiver = callExpression.receiver
            if (receiver is EConcatenationExpression) {
                val valueArgument = callExpression.valueArguments[0]
                valueArgument.parent = receiver
                receiver.expressions.add(valueArgument)
                return receiver
            }
            return EConcatenationExpression(
                callExpression.location,
                Core.Kt.C_String.toType(),
                arrayListOf(callExpression.receiver!!, callExpression.valueArguments[0])
            )
        }
    }
}
