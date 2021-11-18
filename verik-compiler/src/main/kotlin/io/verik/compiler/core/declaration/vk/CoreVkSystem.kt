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
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

object CoreVkSystem : CoreScope(CorePackage.VK) {

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

    val F_time = BasicCoreFunctionDeclaration(parent, "time", "fun time()", Target.F_time)
}
