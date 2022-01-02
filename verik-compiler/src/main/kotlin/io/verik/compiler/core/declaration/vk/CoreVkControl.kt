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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EForkStatement
import io.verik.compiler.ast.element.sv.EWaitForkStatement
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.Target

object CoreVkControl : CoreScope(CorePackage.VK) {

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

    val F_on_Event_Event_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "on",
        "fun on(Event, vararg Event, Function)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
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
            return EForeverStatement(
                callExpression.location,
                functionLiteralExpression.body
            )
        }
    }

    val F_delay_Int = object : TransformableCoreFunctionDeclaration(parent, "delay", "fun delay(Int)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EDelayExpression(callExpression.location, callExpression.valueArguments[0])
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

    val F_fork_Function = object : TransformableCoreFunctionDeclaration(parent, "fork", "fun fork(Function)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteralExpression = callExpression
                .valueArguments[0]
                .cast<EFunctionLiteralExpression>()
            return EForkStatement(callExpression.location, functionLiteralExpression.body)
        }
    }

    val F_join = object : TransformableCoreFunctionDeclaration(parent, "join", "fun join()") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EWaitForkStatement(callExpression.location)
        }
    }
}
