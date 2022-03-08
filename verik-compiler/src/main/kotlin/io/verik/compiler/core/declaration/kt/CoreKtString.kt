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

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

object CoreKtString : CoreScope(Core.Kt.C_String) {

    val F_plus_Any = object : TransformableCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Int)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            val receiver = callExpression.receiver
            if (receiver is EConcatenationExpression) {
                val valueArgument = callExpression.valueArguments[0]
                valueArgument.parent = receiver
                receiver.expressions.add(valueArgument)
                return receiver
            }
            return EConcatenationExpression(
                callExpression.location,
                Core.Kt.C_String.toType(),
                arrayListOf(callExpression.receiver!!, callExpression.valueArguments[0])
            )
        }
    }
}
