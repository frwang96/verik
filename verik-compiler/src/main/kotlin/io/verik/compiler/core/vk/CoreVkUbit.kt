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

package io.verik.compiler.core.vk

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.specialize.BinaryOperatorTypeConstraint
import io.verik.compiler.specialize.BinaryOperatorTypeConstraintKind
import io.verik.compiler.specialize.TypeArgumentTypeConstraint
import io.verik.compiler.specialize.TypeConstraint
import io.verik.compiler.specialize.TypeEqualsTypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_UBIT) {

    val F_INV = CoreKtFunctionDeclaration(parent, "inv")

    val F_GET_INT = object : CoreKtFunctionDeclaration(parent, "get", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_SET_INT_BOOLEAN = object : CoreKtFunctionDeclaration(parent, "set", Core.Kt.C_INT, Core.Kt.C_BOOLEAN) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
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

    val F_PLUS_UBIT = object : CoreKtFunctionDeclaration(parent, "plus", Core.Vk.C_UBIT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    callExpression.receiver!!,
                    callExpression.valueArguments[0],
                    callExpression,
                    BinaryOperatorTypeConstraintKind.MAX
                )
            )
        }
    }

    val F_ADD_UBIT = object : CoreKtFunctionDeclaration(parent, "add", Core.Vk.C_UBIT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    callExpression.receiver!!,
                    callExpression.valueArguments[0],
                    callExpression,
                    BinaryOperatorTypeConstraintKind.MAX_INC
                )
            )
        }
    }

    val F_SHL_INT = object : CoreKtFunctionDeclaration(parent, "shl", Core.Kt.C_INT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeEqualsTypeConstraint(callExpression.receiver!!, callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.LTLT
            )
        }
    }

    val F_SHR_INT = object : CoreKtFunctionDeclaration(parent, "shr", Core.Kt.C_INT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeEqualsTypeConstraint(callExpression.receiver!!, callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGT
            )
        }
    }

    val F_EXT = object : CoreKtFunctionDeclaration(parent, "ext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf(0)))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression? {
            return callExpression.receiver!!
        }
    }
}
