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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.ERepeatStatement
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

object CoreKt : CoreScope(CorePackage.KT) {

    val F_repeat_Int_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "repeat",
        "fun repeat(Int, Function)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteralExpression = callExpression
                .valueArguments[1]
                .cast<EFunctionLiteralExpression>()
            return if (functionLiteralExpression != null) {
                ERepeatStatement(
                    callExpression.location,
                    callExpression.valueArguments[0],
                    functionLiteralExpression.body
                )
            } else callExpression
        }
    }

    val F_assert_Boolean = object : TransformableCoreFunctionDeclaration(parent, "assert", "fun assert(Boolean)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EImmediateAssertStatement(
                callExpression.location,
                Core.Kt.C_Unit.toType(),
                callExpression.valueArguments[0],
                null
            )
        }
    }

    val F_assert_Boolean_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "assert",
        "fun assert(Boolean, Function)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteralExpression = callExpression.valueArguments[1].cast<EFunctionLiteralExpression>()
                ?: return callExpression
            return EImmediateAssertStatement(
                callExpression.location,
                Core.Kt.C_Unit.toType(),
                callExpression.valueArguments[0],
                functionLiteralExpression.body
            )
        }
    }
}
