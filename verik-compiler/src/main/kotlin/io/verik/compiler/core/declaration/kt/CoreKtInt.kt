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
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreBinaryFunctionDeclaration
import io.verik.compiler.core.common.CoreScope

object CoreKtInt : CoreScope(Core.Kt.C_Int) {

    val F_times_Int = object : CoreBinaryFunctionDeclaration(parent, "times", Core.Kt.C_Int) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null)
                (left * right).toString()
            else null
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MUL
        }
    }

    val F_plus_Int = object : CoreBinaryFunctionDeclaration(parent, "plus", Core.Kt.C_Int) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null)
                (left + right).toString()
            else null
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_minus_Int = object : CoreBinaryFunctionDeclaration(parent, "minus", Core.Kt.C_Int) {

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getInt(callExpression.receiver!!)
            val right = ConstantUtil.getInt(callExpression.valueArguments[0])
            return if (left != null && right != null)
                (left - right).toString()
            else null
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MINUS
        }
    }

    val F_lt_Int = object : CoreBinaryFunctionDeclaration(parent, "lt", Core.Kt.C_Int) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.LT
        }
    }

    val F_lteq_Int = object : CoreBinaryFunctionDeclaration(parent, "lteq", Core.Kt.C_Int) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.LTEQ
        }
    }

    val F_gt_Int = object : CoreBinaryFunctionDeclaration(parent, "gt", Core.Kt.C_Int) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.GT
        }
    }

    val F_gteq_Int = object : CoreBinaryFunctionDeclaration(parent, "gteq", Core.Kt.C_Int) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.GTEQ
        }
    }
}
