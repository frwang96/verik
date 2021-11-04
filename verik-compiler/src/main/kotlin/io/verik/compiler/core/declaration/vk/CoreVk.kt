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
import io.verik.compiler.common.BitConstant
import io.verik.compiler.common.Cardinal
import io.verik.compiler.common.ConstantUtil
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.ConcatenationTypeConstraint
import io.verik.compiler.resolve.ReplicationTypeConstraint
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeEqualsTypeConstraint
import io.verik.compiler.resolve.UnaryOperatorTypeConstraint
import io.verik.compiler.resolve.UnaryOperatorTypeConstraintKind
import io.verik.compiler.target.common.Target

object CoreVk : CoreScope(CorePackage.VK) {

    val F_nc = object : TransformableCoreFunctionDeclaration(parent, "nc", "fun nc()") {

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

    val F_i = object : TransformableCoreFunctionDeclaration(parent, "i", "fun i()") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                value.toString()
            )
        }
    }

    val F_u = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u()") {

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

    val F_u_Int = BasicCoreFunctionDeclaration(parent, "u", "fun u(Int)", null)

    val F_u_String = BasicCoreFunctionDeclaration(parent, "u", "fun u(String)", null)

    val F_u_Sbit = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u(Sbit)") {

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

    val F_u0 = object : TransformableCoreFunctionDeclaration(parent, "u0", "fun u0()") {

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

    val F_s_Ubit = object : TransformableCoreFunctionDeclaration(parent, "s", "fun s(Ubit)") {

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

    val F_cat_Any = object : TransformableCoreFunctionDeclaration(parent, "cat", "fun cat(vararg Any)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ConcatenationTypeConstraint(callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            if (callExpression.valueArguments.size < 2)
                Messages.CAT_INSUFFICIENT_ARGUMENTS.on(callExpression)
            return EConcatenationExpression(callExpression.location, callExpression.type, callExpression.valueArguments)
        }
    }

    val F_rep_Any = object : TransformableCoreFunctionDeclaration(parent, "rep", "fun rep(Any)") {

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

    val F_random = BasicCoreFunctionDeclaration(parent, "random", "fun random()", Target.F_random)

    val F_random_Int = BasicCoreFunctionDeclaration(parent, "random", "fun random(Int)", Target.F_urandom_range)

    val F_random_Int_Int = BasicCoreFunctionDeclaration(
        parent,
        "random",
        null,
        Target.F_urandom_range
    )

    val F_randomBoolean = BasicCoreFunctionDeclaration(
        parent,
        "randomBoolean",
        "fun randomBoolean()",
        Target.F_urandom
    )

    val F_randomUbit = object : BasicCoreFunctionDeclaration(
        parent,
        "randomUbit",
        "fun randomUbit()",
        Target.F_urandom
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeEqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }
    }

    val F_forever_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "forever",
        "fun forever(Function)"
    ) {

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

    val F_on_Event_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "on",
        "fun on(Event, Function)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_posedge_Boolean = object : TransformableCoreFunctionDeclaration(parent, "posedge", "fun posedge(Boolean)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.POSEDGE
            )
        }
    }

    val F_negedge_Boolean = object : TransformableCoreFunctionDeclaration(parent, "negedge", "fun negedge(Boolean)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.NEGEDGE
            )
        }
    }

    val F_wait_Boolean = BasicCoreFunctionDeclaration(parent, "wait", "fun wait(Boolean)", Target.F_wait)

    val F_wait_Event = object : TransformableCoreFunctionDeclaration(parent, "wait", "fun wait(Event)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_wait_ClockingBlock = object : TransformableCoreFunctionDeclaration(
        parent,
        "wait",
        "fun wait(ClockingBlock)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_delay_Int = object : TransformableCoreFunctionDeclaration(parent, "delay", "fun delay(Int)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EDelayExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_time = BasicCoreFunctionDeclaration(parent, "time", "fun time()", Target.F_time)

    val F_finish = BasicCoreFunctionDeclaration(parent, "finish", "fun finish()", Target.F_finish)

    val F_fatal = BasicCoreFunctionDeclaration(parent, "fatal", "fun fatal()", Target.F_fatal)

    val F_fatal_String = object : TransformableCoreFunctionDeclaration(parent, "fatal", "fun fatal(String)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EKtCallExpression(
                callExpression.location,
                callExpression.type,
                Target.F_fatal,
                null,
                arrayListOf(
                    EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "1"),
                    callExpression.valueArguments[0]
                ),
                ArrayList()
            )
        }
    }

    val F_error_String = BasicCoreFunctionDeclaration(parent, "error", "fun error(String)", Target.F_error)

    val F_sv_String = BasicCoreFunctionDeclaration(parent, "sv", "fun sv(String)", null)

    val F_Boolean_uext = object : TransformableCoreFunctionDeclaration(parent, "uext", "fun uext()") {

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

    val F_Boolean_sext = object : TransformableCoreFunctionDeclaration(parent, "sext", "fun sext()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_Boolean_uext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = EKtCallExpression(
                callExpression.location,
                Core.Vk.C_Sbit.toType(Cardinal.of(1).toType()),
                Target.F_signed,
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
                Target.F_unsigned,
                null,
                arrayListOf(widthCastExpression),
                ArrayList()
            )
        }
    }
}
