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

package io.verik.compiler.core.lang.vk

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.specialize.ExpressionEqualsTypeConstraint
import io.verik.compiler.specialize.MaxBitWidthTypeConstraint
import io.verik.compiler.specialize.TypeArgumentTypeConstraint
import io.verik.compiler.specialize.TypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_UBIT) {

    val F_INV = CoreKtFunctionDeclaration(parent, "inv")

    val F_PLUS_UBIT = object : CoreKtFunctionDeclaration(parent, "plus", Core.Vk.C_UBIT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                MaxBitWidthTypeConstraint(
                    callExpression.receiver!!,
                    callExpression.valueArguments[0],
                    callExpression
                )
            )
        }
    }

    val F_SHL_INT = object : CoreKtFunctionDeclaration(parent, "shl", Core.Kt.C_INT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ExpressionEqualsTypeConstraint(callExpression.receiver!!, callExpression))
        }
    }

    val F_SHR_INT = object : CoreKtFunctionDeclaration(parent, "shr", Core.Kt.C_INT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ExpressionEqualsTypeConstraint(callExpression.receiver!!, callExpression))
        }
    }

    val F_EXT = object : CoreKtFunctionDeclaration(parent, "ext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf(0)))
        }
    }
}
