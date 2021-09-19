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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.BitConstantUtil
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.message.Messages
import io.verik.compiler.specialize.CardinalBitConstantTypeConstraint
import io.verik.compiler.specialize.TypeArgumentTypeConstraint
import io.verik.compiler.specialize.TypeConstraint

object CoreVk : CoreScope(CorePackage.VK) {

    val N_ADD = CoreCardinalFunctionDeclaration("ADD")
    val N_SUB = CoreCardinalFunctionDeclaration("SUB")
    val N_MUL = CoreCardinalFunctionDeclaration("MUL")
    val N_MAX = CoreCardinalFunctionDeclaration("MAX")
    val N_MIN = CoreCardinalFunctionDeclaration("MIN")
    val N_INC = CoreCardinalFunctionDeclaration("INC")
    val N_DEC = CoreCardinalFunctionDeclaration("DEC")
    val N_LOG = CoreCardinalFunctionDeclaration("LOG")
    val N_INCLOG = CoreCardinalFunctionDeclaration("INCLOG")
    val N_EXP = CoreCardinalFunctionDeclaration("EXP")

    val F_NC = object : CoreKtFunctionDeclaration(parent, "nc") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf()))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression? {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return null
        }
    }

    val F_U = object : CoreKtFunctionDeclaration(parent, "u") {

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

    val F_U_INT = CoreKtFunctionDeclaration(parent, "u", Core.Kt.C_INT)

    val F_ZEROES = object : CoreKtFunctionDeclaration(parent, "zeroes") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf()))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            return EConstantExpression(callExpression.location, callExpression.type, BitConstantUtil.format(0, width))
        }
    }

    val F_RANDOM = CoreKtFunctionDeclaration(parent, "random")
    val F_RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", Core.Kt.C_INT)
    val F_FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", Core.Kt.C_FUNCTION)

    val F_ON_EVENT_FUNCTION = object : CoreKtFunctionDeclaration(parent, "on", Core.Vk.C_EVENT, Core.Kt.C_FUNCTION) {

        override fun transform(callExpression: EKtCallExpression): EExpression? {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return null
        }
    }

    val F_POSEDGE_BOOLEAN = object : CoreKtFunctionDeclaration(parent, "posedge", Core.Kt.C_BOOLEAN) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.POSEDGE
            )
        }
    }

    val F_NEGEDGE_BOOLEAN = object : CoreKtFunctionDeclaration(parent, "negedge", Core.Kt.C_BOOLEAN) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventExpression(
                callExpression.location,
                callExpression.valueArguments[0],
                EdgeType.NEGEDGE
            )
        }
    }

    val F_WAIT_EVENT = object : CoreKtFunctionDeclaration(parent, "wait", Core.Vk.C_EVENT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_DELAY_INT = object : CoreKtFunctionDeclaration(parent, "delay", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EDelayExpression(callExpression.location, callExpression.valueArguments[0])
        }
    }

    val F_TIME = CoreKtFunctionDeclaration(parent, "time")
    val F_FINISH = CoreKtFunctionDeclaration(parent, "finish")

    val F_SV_STRING = CoreKtFunctionDeclaration(parent, "sv", Core.Kt.C_STRING)
}
