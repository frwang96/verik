/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

/**
 * Core declarations from the Kotlin collections package.
 */
object CoreKtCollections : CoreScope(CorePackage.KT_COLLECTIONS) {

    val F_forEach_Function = object : BasicCoreFunctionDeclaration(
        parent,
        "forEach",
        "fun forEach(Function)",
        null
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            val functionLiteralExpression = callExpression.valueArguments[0] as EFunctionLiteralExpression
            if (callExpression.receiver!!.type.arguments.isEmpty()) return listOf()
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(functionLiteralExpression.valueParameters[0]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }
}
