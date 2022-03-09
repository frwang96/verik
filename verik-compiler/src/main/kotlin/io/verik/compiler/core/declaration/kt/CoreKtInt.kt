/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.constant.IntConstantEvaluator
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration

object CoreKtInt : CoreScope(Core.Kt.C_Int) {

    val F_unaryPlus = object : TransformableCoreFunctionDeclaration(parent, "unaryPlus", "fun unaryPlus()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return callExpression.receiver!!
        }
    }

    val F_unaryMinus = UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    )

    val F_rangeTo_Int = BasicCoreFunctionDeclaration(parent, "rangeTo", "fun rangeTo(Int)", null)

    val F_plus_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Int)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun evaluate(callExpression: ECallExpression): EConstantExpression? {
            val left = callExpression.receiver!!
            val right = callExpression.valueArguments[0]
            return if (left is EConstantExpression && right is EConstantExpression) {
                IntConstantEvaluator.plusInt(callExpression, left, right)
            } else null
        }
    }

    val F_minus_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Int)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun evaluate(callExpression: ECallExpression): EConstantExpression? {
            val left = callExpression.receiver!!
            val right = callExpression.valueArguments[0]
            return if (left is EConstantExpression && right is EConstantExpression) {
                IntConstantEvaluator.minusInt(callExpression, left, right)
            } else null
        }
    }

    val F_times_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Int)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun evaluate(callExpression: ECallExpression): EConstantExpression? {
            val left = callExpression.receiver!!
            val right = callExpression.valueArguments[0]
            return if (left is EConstantExpression && right is EConstantExpression) {
                IntConstantEvaluator.timesInt(callExpression, left, right)
            } else null
        }
    }

    val F_shl_Int = BinaryCoreFunctionDeclaration(parent, "shl", "fun shl(Int)", SvBinaryOperatorKind.LTLT)

    val F_shr_Int = BinaryCoreFunctionDeclaration(parent, "shr", "fun shr(Int)", SvBinaryOperatorKind.GTGTGT)

    val F_ushr_Int = BinaryCoreFunctionDeclaration(parent, "ushr", "fun ushr(Int)", SvBinaryOperatorKind.GTGT)
}
