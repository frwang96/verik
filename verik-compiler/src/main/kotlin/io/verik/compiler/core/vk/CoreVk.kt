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
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.BitConstantUtil
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtBasicFunctionDeclaration
import io.verik.compiler.core.common.CoreKtTransformableFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.message.Messages
import io.verik.compiler.specialize.CardinalBitConstantTypeConstraint
import io.verik.compiler.specialize.ConcatenationTypeConstraint
import io.verik.compiler.specialize.TypeArgumentTypeConstraint
import io.verik.compiler.specialize.TypeConstraint

object CoreVk : CoreScope(CorePackage.VK) {

    val F_NC = object : CoreKtTransformableFunctionDeclaration(parent, "nc") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf()))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_U = object : CoreKtTransformableFunctionDeclaration(parent, "u") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(CardinalBitConstantTypeConstraint(callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val width = callExpression.type.asBitWidth(callExpression)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                BitConstantUtil.format(value, width)
            )
        }
    }

    val F_U_INT = CoreKtBasicFunctionDeclaration(parent, "u", Core.Kt.C_INT)

    val F_ZEROES = object : CoreKtTransformableFunctionDeclaration(parent, "zeroes") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf()))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            return EConstantExpression(callExpression.location, callExpression.type, BitConstantUtil.format(0, width))
        }
    }

    val F_CAT = object : CoreKtTransformableFunctionDeclaration(parent, "cat", Core.Kt.C_ANY) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(ConcatenationTypeConstraint(callExpression))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            if (callExpression.valueArguments.size < 2)
                Messages.CAT_INSUFFICIENT_ARGUMENTS.on(callExpression)
            return EConcatenationExpression(callExpression.location, callExpression.type, callExpression.valueArguments)
        }
    }

    val F_RANDOM = object : CoreKtTransformableFunctionDeclaration(parent, "random") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_RANDOM
            return callExpression
        }
    }

    val F_RANDOM_INT = CoreKtBasicFunctionDeclaration(parent, "random", Core.Kt.C_INT)

    val F_RANDOM_UBIT = object : CoreKtTransformableFunctionDeclaration(parent, "randomUbit") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf(0)))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_URANDOM
            return callExpression
        }
    }

    val F_FOREVER_FUNCTION = CoreKtBasicFunctionDeclaration(parent, "forever", Core.Kt.C_FUNCTION)

    val F_ON_EVENT_FUNCTION = object : CoreKtTransformableFunctionDeclaration(
        parent,
        "on",
        Core.Vk.C_EVENT,
        Core.Kt.C_FUNCTION
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_POSEDGE_BOOLEAN = object : CoreKtTransformableFunctionDeclaration(parent, "posedge", Core.Kt.C_BOOLEAN) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.POSEDGE
            )
        }
    }

    val F_NEGEDGE_BOOLEAN = object : CoreKtTransformableFunctionDeclaration(parent, "negedge", Core.Kt.C_BOOLEAN) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.NEGEDGE
            )
        }
    }

    val F_WAIT_EVENT = object : CoreKtTransformableFunctionDeclaration(parent, "wait", Core.Vk.C_EVENT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_DELAY_INT = object : CoreKtTransformableFunctionDeclaration(parent, "delay", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EDelayExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_TIME = object : CoreKtTransformableFunctionDeclaration(parent, "time") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_TIME
            return callExpression
        }
    }

    val F_FINISH = object : CoreKtTransformableFunctionDeclaration(parent, "finish") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_FINISH
            return callExpression
        }
    }

    val F_SV_STRING = CoreKtBasicFunctionDeclaration(parent, "sv", Core.Kt.C_STRING)
}
