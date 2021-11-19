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

package io.verik.compiler.core.declaration.vk

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
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.resolve.BinaryOperatorTypeConstraint
import io.verik.compiler.resolve.BinaryOperatorTypeConstraintKind
import io.verik.compiler.resolve.ComparisonTypeConstraint
import io.verik.compiler.resolve.ComparisonTypeConstraintKind
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeEqualsTypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_Ubit) {

    val F_unaryMinus = object : UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }
    }

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_set_Int_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Int, Boolean)"
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

    val F_set_Int_Ubit = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, Ubit)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.valueArguments[1].type.asBitWidth(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_plus_Int,
                ExpressionCopier.copy(callExpression.valueArguments[0]),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${width - 1}")),
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

    val F_plus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

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

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getBitConstant(callExpression.receiver!!)
            val right = ConstantUtil.getBitConstant(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                ConstantUtil.formatBitConstant(left.add(right, callExpression))
            } else null
        }
    }

    val F_add_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "add",
        "fun add(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

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
    }

    val F_minus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Ubit)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantUtil.getBitConstant(callExpression.receiver!!)
            val right = ConstantUtil.getBitConstant(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                ConstantUtil.formatBitConstant(left.sub(right, callExpression))
            } else null
        }
    }

    val F_times_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_mul_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "mul",
        "fun mul(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

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
    }

    val F_div_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "div",
        "fun div(Ubit)",
        SvBinaryOperatorKind.DIV
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }
    }

    val F_and_Ubit = object : BinaryCoreFunctionDeclaration(parent, "and", "fun and(Ubit)", SvBinaryOperatorKind.AND) {

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
    }

    val F_or_Ubit = object : BinaryCoreFunctionDeclaration(parent, "or", "fun or(Ubit)", SvBinaryOperatorKind.OR) {

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
    }

    val F_xor_Ubit = object : BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Ubit)", SvBinaryOperatorKind.XOR) {

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
    }

    val F_shl_Int = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Int)") {

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

    val F_shl_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_shl_Int.transform(callExpression)
        }
    }

    val F_shr_Int = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Int)") {

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

    val F_shr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_shr_Int.transform(callExpression)
        }
    }

    val F_sshr_Int = object : TransformableCoreFunctionDeclaration(parent, "sshr", "fun sshr(Int)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val binaryExpression = ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGTGT
            )
            return CoreTransformUtil.callExpressionUnsigned(binaryExpression)
        }
    }

    val F_sshr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "sshr", "fun sshr(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_sshr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_sshr_Int.transform(callExpression)
        }
    }

    val F_invert = object : UnaryCoreFunctionDeclaration(
        parent,
        "invert",
        "fun invert()",
        SvUnaryOperatorKind.BITWISE_NEG
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }
    }

    val F_reverse = object : TransformableCoreFunctionDeclaration(parent, "reverse", "fun reverse()") {

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

    val F_slice_Int = object : TransformableCoreFunctionDeclaration(parent, "slice", "fun slice(Int)") {

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

    val F_ext = object : TransformableCoreFunctionDeclaration(parent, "ext", "fun ext()") {

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

    val F_sext = object : TransformableCoreFunctionDeclaration(parent, "sext", "fun sext()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_ext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                value
            )
        }
    }

    val F_tru = object : TransformableCoreFunctionDeclaration(parent, "tru", "fun tru()") {

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
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                value
            )
        }
    }
}
