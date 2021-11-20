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

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope

object CoreKtInt : CoreScope(Core.Kt.C_Int) {

    val F_plus_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Int)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                (left + right).toString()
            } else null
        }
    }

    val F_minus_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Int)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null)
                (left - right).toString()
            else null
        }
    }

    val F_times_Int = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Int)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                (left * right).toString()
            } else null
        }
    }
}
