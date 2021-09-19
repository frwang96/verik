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

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
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
    val N_EXP = CoreCardinalFunctionDeclaration("EXP")

    val F_NC = CoreKtFunctionDeclaration(parent, "nc")
    val F_U_INT = CoreKtFunctionDeclaration(parent, "u", Core.Kt.C_INT)

    val F_ZEROES = object : CoreKtFunctionDeclaration(parent, "zeroes") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(TypeArgumentTypeConstraint(callExpression, listOf()))
        }
    }

    val F_RANDOM = CoreKtFunctionDeclaration(parent, "random")
    val F_RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", Core.Kt.C_INT)
    val F_FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", Core.Kt.C_FUNCTION)
    val F_ON_EVENT_FUNCTION = CoreKtFunctionDeclaration(parent, "on", Core.Vk.C_EVENT, Core.Kt.C_FUNCTION)

    val F_POSEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "posedge", Core.Kt.C_BOOLEAN)
    val F_NEGEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "negedge", Core.Kt.C_BOOLEAN)
    val F_WAIT_EVENT = CoreKtFunctionDeclaration(parent, "wait", Core.Vk.C_EVENT)
    val F_DELAY_INT = CoreKtFunctionDeclaration(parent, "delay", Core.Kt.C_INT)
    val F_TIME = CoreKtFunctionDeclaration(parent, "time")
    val F_FINISH = CoreKtFunctionDeclaration(parent, "finish")

    val F_SV_STRING = CoreKtFunctionDeclaration(parent, "sv", Core.Kt.C_STRING)
}
