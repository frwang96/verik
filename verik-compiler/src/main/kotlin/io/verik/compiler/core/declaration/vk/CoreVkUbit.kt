/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.EStreamingExpression
import io.verik.compiler.ast.element.expression.sv.EStringExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.expression.sv.EWidthCastExpression
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
import io.verik.compiler.target.common.Target

/**
 * Core declarations from Ubit excluding binary functions.
 */
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

    val F_get_Int_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int, Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_OUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                callExpression.valueArguments[1]
            )
        }
    }

    val F_set_Int_Boolean = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val arrayAccessExpression = ESvArrayAccessExpression(
                callExpression.location,
                callExpression.valueArguments[1].type.copy(),
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
            return EKtBinaryExpression(
                callExpression.location,
                callExpression.type,
                arrayAccessExpression,
                callExpression.valueArguments[1],
                KtBinaryOperatorKind.EQ
            )
        }
    }

    val F_set_Ubit_Boolean = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Ubit, Boolean)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_set_Int_Boolean.transform(callExpression)
        }
    }

    val F_set_Int_Int_Ubit = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, Int, Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[2], 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val constantPartSelectExpression = EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                callExpression.valueArguments[1]
            )
            return EKtBinaryExpression(
                callExpression.location,
                callExpression.type.copy(),
                constantPartSelectExpression,
                callExpression.valueArguments[2],
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

    val F_ext = object : TransformableCoreFunctionDeclaration(parent, "ext", "fun ext()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
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

    val F_toSbit = object : TransformableCoreFunctionDeclaration(parent, "toSbit", "fun toSbit()") {

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
            return CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
        }
    }

    val F_toBinString = object : TransformableCoreFunctionDeclaration(parent, "toBinString", "fun toBinString()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val stringExpression = EStringExpression(callExpression.location, "%b")
            return ECallExpression(
                callExpression.location,
                Core.Kt.C_String.toType(),
                Target.F_sformatf,
                null,
                false,
                arrayListOf(stringExpression, callExpression.receiver!!),
                ArrayList()
            )
        }
    }

    val F_toDecString = object : TransformableCoreFunctionDeclaration(parent, "toDecString", "fun toDecString()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val stringExpression = EStringExpression(callExpression.location, "%0d")
            return ECallExpression(
                callExpression.location,
                Core.Kt.C_String.toType(),
                Target.F_sformatf,
                null,
                false,
                arrayListOf(stringExpression, callExpression.receiver!!),
                ArrayList()
            )
        }
    }

    val F_toHexString = object : TransformableCoreFunctionDeclaration(parent, "toHexString", "fun toHexString()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val stringExpression = EStringExpression(callExpression.location, "%h")
            return ECallExpression(
                callExpression.location,
                Core.Kt.C_String.toType(),
                Target.F_sformatf,
                null,
                false,
                arrayListOf(stringExpression, callExpression.receiver!!),
                ArrayList()
            )
        }
    }
}
