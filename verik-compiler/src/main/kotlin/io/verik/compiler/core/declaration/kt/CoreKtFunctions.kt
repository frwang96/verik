/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.expression.sv.ERepeatStatement
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

object CoreKtFunctions : CoreScope(CorePackage.KT) {

    val F_repeat_Int_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "repeat",
        "fun repeat(Int, Function)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            val functionLiteralExpression = callExpression
                .valueArguments[1]
                .cast<EFunctionLiteralExpression>()
            return ERepeatStatement(
                callExpression.location,
                callExpression.valueArguments[0],
                functionLiteralExpression.body
            )
        }
    }

    val F_assert_Boolean = object : TransformableCoreFunctionDeclaration(parent, "assert", "fun assert(Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return EImmediateAssertStatement(
                callExpression.location,
                callExpression.valueArguments[0],
                null
            )
        }
    }

    val F_assert_Boolean_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "assert",
        "fun assert(Boolean, Function)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            val functionLiteralExpression = callExpression.valueArguments[1].cast<EFunctionLiteralExpression>()
            return EImmediateAssertStatement(
                callExpression.location,
                callExpression.valueArguments[0],
                functionLiteralExpression.body
            )
        }
    }
}
