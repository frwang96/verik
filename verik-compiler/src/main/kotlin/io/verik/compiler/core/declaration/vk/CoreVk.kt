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
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.collateral.common.Collateral
import io.verik.compiler.common.BitConstant
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreBasicFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformableFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.ConcatenationTypeConstraint
import io.verik.compiler.resolve.ReplicationTypeConstraint
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeEqualsTypeConstraint
import io.verik.compiler.resolve.UnaryOperatorTypeConstraint
import io.verik.compiler.resolve.UnaryOperatorTypeConstraintKind

object CoreVk : CoreScope(CorePackage.VK) {

    val F_nc = object : CoreTransformableFunctionDeclaration(parent, "nc") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_i = object : CoreTransformableFunctionDeclaration(parent, "i") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                value.toString()
            )
        }
    }

    val F_u = object : CoreTransformableFunctionDeclaration(parent, "u") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                UnaryOperatorTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    true,
                    UnaryOperatorTypeConstraintKind.WIDTH
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val width = callExpression.type.arguments[0].asCardinalValue(callExpression)
            val bitConstant = BitConstant(value, width)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                ConstantUtil.formatBitConstant(bitConstant)
            )
        }
    }

    val F_u_Int = CoreBasicFunctionDeclaration(parent, "u", Core.Kt.C_Int)

    val F_u_String = CoreBasicFunctionDeclaration(parent, "u", Core.Kt.C_String)

    val F_u_Sbit = object : CoreTransformableFunctionDeclaration(parent, "u", Core.Vk.C_Sbit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return callExpression.valueArguments[0]
        }
    }

    val F_u0 = object : CoreTransformableFunctionDeclaration(parent, "u0") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.arguments[0].asCardinalValue(callExpression)
            val bitConstant = BitConstant(0, width)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                ConstantUtil.formatBitConstant(bitConstant)
            )
        }
    }

    val F_s_Ubit = object : CoreTransformableFunctionDeclaration(parent, "s", Core.Vk.C_Ubit) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return callExpression.valueArguments[0]
        }
    }

    val F_cat = object : CoreTransformableFunctionDeclaration(parent, "cat", Core.Kt.C_Any) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ConcatenationTypeConstraint(callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            if (callExpression.valueArguments.size < 2)
                Messages.CAT_INSUFFICIENT_ARGUMENTS.on(callExpression)
            return EConcatenationExpression(callExpression.location, callExpression.type, callExpression.valueArguments)
        }
    }

    val F_rep = object : CoreTransformableFunctionDeclaration(parent, "rep", Core.Kt.C_Any) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ReplicationTypeConstraint(callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EReplicationExpression(
                callExpression.location,
                callExpression.type,
                callExpression.valueArguments[0],
                callExpression.typeArguments[0].asCardinalValue(callExpression)
            )
        }
    }

    val F_random = object : CoreTransformableFunctionDeclaration(parent, "random") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_random
            return callExpression
        }
    }

    val F_random_Int = object : CoreTransformableFunctionDeclaration(parent, "random", Core.Kt.C_Int) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_urandom_range
            return callExpression
        }
    }

    val F_random_Int_Int = object : CoreTransformableFunctionDeclaration(
        parent,
        "random",
        Core.Kt.C_Int,
        Core.Kt.C_Int
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_urandom_range
            return callExpression
        }
    }

    val F_randomBoolean = object : CoreTransformableFunctionDeclaration(parent, "randomBoolean") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_urandom
            return callExpression
        }
    }

    val F_randomUbit = object : CoreTransformableFunctionDeclaration(parent, "randomUbit") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_urandom
            return callExpression
        }
    }

    val F_forever_Function = object : CoreTransformableFunctionDeclaration(parent, "forever", Core.Kt.C_Function) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteralExpression = callExpression
                .valueArguments[0]
                .cast<EFunctionLiteralExpression>()
            return if (functionLiteralExpression != null) {
                EForeverStatement(
                    callExpression.location,
                    functionLiteralExpression.body
                )
            } else callExpression
        }
    }

    val F_on_Event_Function = object : CoreTransformableFunctionDeclaration(
        parent,
        "on",
        Core.Vk.C_Event,
        Core.Kt.C_Function
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_posedge_Boolean = object : CoreTransformableFunctionDeclaration(parent, "posedge", Core.Kt.C_Boolean) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.POSEDGE
            )
        }
    }

    val F_negedge_Boolean = object : CoreTransformableFunctionDeclaration(parent, "negedge", Core.Kt.C_Boolean) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.NEGEDGE
            )
        }
    }

    val F_wait_Boolean = object : CoreTransformableFunctionDeclaration(parent, "wait", Core.Kt.C_Boolean) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_wait
            return callExpression
        }
    }

    val F_wait_Event = object : CoreTransformableFunctionDeclaration(parent, "wait", Core.Vk.C_Event) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_wait_ClockingBlock = object : CoreTransformableFunctionDeclaration(
        parent,
        "wait",
        Core.Vk.C_ClockingBlock
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_delay_Int = object : CoreTransformableFunctionDeclaration(parent, "delay", Core.Kt.C_Int) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EDelayExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_time = object : CoreTransformableFunctionDeclaration(parent, "time") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_time
            return callExpression
        }
    }

    val F_finish = object : CoreTransformableFunctionDeclaration(parent, "finish") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_finish
            return callExpression
        }
    }

    val F_fatal = object : CoreTransformableFunctionDeclaration(parent, "fatal") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Collateral.System.F_fatal
            return callExpression
        }
    }

    val F_sv_String = CoreBasicFunctionDeclaration(parent, "sv", Core.Kt.C_String)

    val F_Boolean_uext = object : CoreTransformableFunctionDeclaration(parent, "uext") {

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
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                value
            )
        }
    }

    val F_Boolean_sext = object : CoreTransformableFunctionDeclaration(parent, "sext") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_Boolean_uext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = EKtCallExpression(
                callExpression.location,
                Core.Vk.C_Sbit.toType(Core.Vk.cardinalOf(1).toType()),
                Collateral.System.F_signed,
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
                Collateral.System.F_unsigned,
                null,
                arrayListOf(widthCastExpression),
                ArrayList()
            )
        }
    }
}
