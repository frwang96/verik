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
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtBinaryFunctionDeclaration
import io.verik.compiler.core.common.CoreKtTransformableFunctionDeclaration
import io.verik.compiler.core.common.CoreKtUnaryFunctionDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.resolve.BinaryOperatorTypeConstraint
import io.verik.compiler.resolve.BinaryOperatorTypeConstraintKind
import io.verik.compiler.resolve.ComparisonTypeConstraint
import io.verik.compiler.resolve.ComparisonTypeConstraintKind
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeEqualsTypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_Ubit) {

    val F_get_Int = object : CoreKtTransformableFunctionDeclaration(parent, "get", Core.Kt.C_Int) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_set_Int_Boolean = object : CoreKtTransformableFunctionDeclaration(
        parent,
        "set",
        Core.Kt.C_Int,
        Core.Kt.C_Boolean
    ) {

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

    val F_set_Int_Ubit = object : CoreKtTransformableFunctionDeclaration(parent, "set", Core.Kt.C_Int, Core.Vk.C_Ubit) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.valueArguments[1].type.arguments[0].asCardinalValue(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_plus_Int,
                ExpressionCopier.copy(callExpression.valueArguments[0]),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${value - 1}")),
                arrayListOf()
            )
            val receiver = EConstantPartSelectExpression(
                callExpression.location,
                callExpression.valueArguments[1].type.copy(),
                callExpression.receiver!!,
                msbIndex,
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

    val F_unaryMinus = object : CoreKtUnaryFunctionDeclaration(parent, "unaryMinus") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun getOperatorKind(): SvUnaryOperatorKind {
            return SvUnaryOperatorKind.MINUS
        }
    }

    val F_plus_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "plus", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryOperatorTypeConstraintKind.MAX
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_add_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "add", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryOperatorTypeConstraintKind.MAX_INC
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_minus_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "minus", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MINUS
        }
    }

    val F_times_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "times", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MUL
        }
    }

    val F_mul_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "mul", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryOperatorTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryOperatorTypeConstraintKind.ADD
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MUL
        }
    }

    val F_div_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "div", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.DIV
        }
    }

    val F_and_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "and", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.AND
        }
    }

    val F_or_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "or", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.OR
        }
    }

    val F_xor_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "xor", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.XOR
        }
    }

    val F_sll_Int = object : CoreKtTransformableFunctionDeclaration(parent, "sll", Core.Kt.C_Int) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
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

    val F_sll_Ubit = object : CoreKtTransformableFunctionDeclaration(parent, "sll", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_sll_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_sll_Int.transform(callExpression)
        }
    }

    val F_srl_Int = object : CoreKtTransformableFunctionDeclaration(parent, "srl", Core.Kt.C_Int) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
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

    val F_srl_Ubit = object : CoreKtTransformableFunctionDeclaration(parent, "srl", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_srl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_srl_Int.transform(callExpression)
        }
    }

    val F_sra_Int = object : CoreKtTransformableFunctionDeclaration(parent, "sra", Core.Kt.C_Int) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = EKtCallExpression(
                callExpression.location,
                Core.Vk.C_Sbit.toType(callExpression.type.arguments[0].copy()),
                Core.Sv.F_signed,
                null,
                arrayListOf(callExpression.receiver!!),
                ArrayList()
            )
            val binaryExpression = ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGTGT
            )
            return EKtCallExpression(
                callExpression.location,
                callExpression.type.copy(),
                Core.Sv.F_unsigned,
                null,
                arrayListOf(binaryExpression),
                ArrayList()
            )
        }
    }

    val F_sra_Ubit = object : CoreKtTransformableFunctionDeclaration(parent, "sra", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_sra_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_sra_Int.transform(callExpression)
        }
    }

    val F_lt_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "lt", Core.Vk.C_Ubit) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.LT
        }
    }

    val F_lteq_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "lteq", Core.Vk.C_Ubit) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.LTEQ
        }
    }

    val F_gt_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "gt", Core.Vk.C_Ubit) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.GT
        }
    }

    val F_gteq_Ubit = object : CoreKtBinaryFunctionDeclaration(parent, "gteq", Core.Vk.C_Ubit) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.GTEQ
        }
    }

    val F_invert = object : CoreKtUnaryFunctionDeclaration(parent, "invert") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun getOperatorKind(): SvUnaryOperatorKind {
            return SvUnaryOperatorKind.BITWISE_NEG
        }
    }

    val F_reverse = object : CoreKtTransformableFunctionDeclaration(parent, "reverse") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EStreamingExpression(callExpression.location, callExpression.type, callExpression.receiver!!)
        }
    }

    val F_uext = object : CoreKtTransformableFunctionDeclaration(parent, "uext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                ComparisonTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    ComparisonTypeConstraintKind.EXT
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                value
            )
        }
    }

    val F_sext = object : CoreKtTransformableFunctionDeclaration(parent, "sext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_uext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = EKtCallExpression(
                callExpression.location,
                Core.Vk.C_Sbit.toType(callExpression.receiver!!.type.arguments[0].copy()),
                Core.Sv.F_signed,
                null,
                arrayListOf(callExpression.receiver!!),
                ArrayList()
            )
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val widthCastExpression = EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                value
            )
            return EKtCallExpression(
                callExpression.location,
                callExpression.type.copy(),
                Core.Sv.F_unsigned,
                null,
                arrayListOf(widthCastExpression),
                ArrayList()
            )
        }
    }

    val F_tru = object : CoreKtTransformableFunctionDeclaration(parent, "tru") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                ComparisonTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    ComparisonTypeConstraintKind.TRU
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${value - 1}")
            val lsbIndex = EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "0")
            return EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                msbIndex,
                lsbIndex
            )
        }
    }

    val F_slice_Int = object : CoreKtTransformableFunctionDeclaration(parent, "slice", Core.Kt.C_Int) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_plus_Int,
                ExpressionCopier.copy(callExpression.valueArguments[0]),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${value - 1}")),
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
