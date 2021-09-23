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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtBasicFunctionDeclaration
import io.verik.compiler.core.common.CoreKtBinaryFunctionDeclaration
import io.verik.compiler.core.common.CoreKtTransformableFunctionDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.specialize.BinaryOperatorTypeConstraint
import io.verik.compiler.specialize.BinaryOperatorTypeConstraintKind
import io.verik.compiler.specialize.TypeArgumentTypeConstraint
import io.verik.compiler.specialize.TypeConstraint
import io.verik.compiler.specialize.TypeEqualsTypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_UBIT) {

    val F_INV = CoreKtBasicFunctionDeclaration(parent, "inv")

    val F_GET_INT = object : CoreKtTransformableFunctionDeclaration(parent, "get", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_SET_INT_BOOLEAN = object : CoreKtTransformableFunctionDeclaration(parent, "set", Core.Kt.C_INT, Core.Kt.C_BOOLEAN) {

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

    val F_PLUS_UBIT = object : CoreKtBinaryFunctionDeclaration(parent, "plus", Core.Vk.C_UBIT) {

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

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_ADD_UBIT = object : CoreKtBinaryFunctionDeclaration(parent, "add", Core.Vk.C_UBIT) {

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

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_MUL_UBIT = object : CoreKtBinaryFunctionDeclaration(parent, "mul", Core.Vk.C_UBIT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    callExpression.receiver!!,
                    callExpression.valueArguments[0],
                    callExpression,
                    BinaryOperatorTypeConstraintKind.ADD
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MUL
        }
    }

    val F_SHL_INT = object : CoreKtTransformableFunctionDeclaration(parent, "shl", Core.Kt.C_INT) {

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

    val F_SHR_INT = object : CoreKtTransformableFunctionDeclaration(parent, "shr", Core.Kt.C_INT) {

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

    val F_EXT = object : CoreKtTransformableFunctionDeclaration(parent, "ext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf(0)))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return callExpression.receiver!!
        }
    }

    val F_SLICE_INT = object : CoreKtTransformableFunctionDeclaration(parent, "slice", Core.Kt.C_INT) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf(0)))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_INT.toType(),
                Core.Kt.Int.F_PLUS_INT,
                callExpression.valueArguments[0].copy(),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_INT.toType(), "${value - 1}")),
                arrayListOf()
            )
            return EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                msbIndex,
                callExpression.valueArguments[0]
            )
        }
    }
}
