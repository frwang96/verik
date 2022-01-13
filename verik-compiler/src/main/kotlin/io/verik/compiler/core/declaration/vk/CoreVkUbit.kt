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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

object CoreVkUbit : CoreScope(Core.Vk.C_Ubit) {

    val F_unaryPlus = object : TransformableCoreFunctionDeclaration(parent, "unaryPlus", "fun unaryPlus()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return callExpression.receiver!!
        }
    }

    val F_unaryMinus = object : UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_unaryPlus.getTypeConstraints(callExpression)
        }
    }

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_get_Ubit = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.LOG_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_get_Int.transform(callExpression)
        }
    }

    val F_set_Int_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Int, Boolean)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
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

    val F_set_Ubit_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Ubit, Boolean)"
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_set_Int_Boolean.transform(callExpression)
        }
    }

    val F_set_Int_Ubit = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, Ubit)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.valueArguments[1].type.asBitWidth(callExpression)
            val msbIndex = CoreTransformUtil.plusInt(
                callExpression.valueArguments[0],
                width - 1,
                callExpression.location
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

    val F_set_Ubit_Ubit = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Ubit, Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.valueArguments[1].type.asBitWidth(callExpression)
            val msbIndex = CoreTransformUtil.plusUbit(
                callExpression.valueArguments[0],
                width - 1,
                callExpression.location
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

    val F_not = UnaryCoreFunctionDeclaration(
        parent,
        "not",
        "fun not()",
        SvUnaryOperatorKind.LOGICAL_NEG
    )

    val F_inv = object : UnaryCoreFunctionDeclaration(
        parent,
        "inv",
        "fun inv()",
        SvUnaryOperatorKind.BITWISE_NEG
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }

    val F_rev = object : TransformableCoreFunctionDeclaration(parent, "rev", "fun rev()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_inv.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return EStreamingExpression(callExpression.location, callExpression.type, callExpression.receiver!!)
        }
    }

    val F_andRed = UnaryCoreFunctionDeclaration(parent, "andRed", "fun andRed()", SvUnaryOperatorKind.AND)

    val F_orRed = UnaryCoreFunctionDeclaration(parent, "orRed", "fun orRed()", SvUnaryOperatorKind.OR)

    val F_xorRed = UnaryCoreFunctionDeclaration(parent, "xorRed", "fun xorRed()", SvUnaryOperatorKind.XOR)

    val F_eqz = object : TransformableCoreFunctionDeclaration(parent, "eqz", "fun eqz()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.receiver!!.type.asBitWidth(callExpression)
            val constantExpression = ConstantBuilder.buildBitConstant(callExpression.location, width, 0)
            return EKtBinaryExpression(
                callExpression.location,
                Core.Kt.C_Boolean.toType(),
                callExpression.receiver!!,
                constantExpression,
                KtBinaryOperatorKind.EQEQ
            )
        }
    }

    val F_neqz = object : TransformableCoreFunctionDeclaration(parent, "neqz", "fun neqz()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.receiver!!.type.asBitWidth(callExpression)
            val constantExpression = ConstantBuilder.buildBitConstant(callExpression.location, width, 0)
            return EKtBinaryExpression(
                callExpression.location,
                Core.Kt.C_Boolean.toType(),
                callExpression.receiver!!,
                constantExpression,
                KtBinaryOperatorKind.EXCL_EQ
            )
        }
    }

    val F_sli_Int = object : TransformableCoreFunctionDeclaration(parent, "sli", "fun sli(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = CoreTransformUtil.plusInt(
                callExpression.valueArguments[0],
                width - 1,
                callExpression.location
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

    val F_sli_Ubit = object : TransformableCoreFunctionDeclaration(parent, "sli", "fun sli(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.LOG_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = CoreTransformUtil.plusUbit(
                callExpression.valueArguments[0],
                width - 1,
                callExpression.location
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

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.EXT_IN,
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                width
            )
        }
    }

    val F_sext = object : TransformableCoreFunctionDeclaration(parent, "sext", "fun sext()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_ext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val widthCastExpression = EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                width
            )
            return CoreTransformUtil.callExpressionUnsigned(widthCastExpression)
        }
    }

    val F_tru = object : TransformableCoreFunctionDeclaration(parent, "tru", "fun tru()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.TRU_IN,
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = ConstantBuilder.buildInt(callExpression, width - 1)
            val lsbIndex = ConstantBuilder.buildInt(callExpression, 0)
            return EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                msbIndex,
                lsbIndex
            )
        }
    }

    val F_res = object : TransformableCoreFunctionDeclaration(parent, "res", "fun res()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val receiverWidth = callExpression.receiver!!.type.asBitWidth(callExpression)
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return when {
                receiverWidth < width -> F_ext.transform(callExpression)
                receiverWidth > width -> F_tru.transform(callExpression)
                else -> callExpression.receiver!!
            }
        }
    }

    val F_sres = object : TransformableCoreFunctionDeclaration(parent, "sres", "fun sres()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_res.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val receiverWidth = callExpression.receiver!!.type.asBitWidth(callExpression)
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return when {
                receiverWidth < width -> F_sext.transform(callExpression)
                receiverWidth > width -> F_tru.transform(callExpression)
                else -> callExpression.receiver!!
            }
        }
    }
}
