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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

object CoreKtIo : CoreScope(CorePackage.KT_IO) {

    val F_print_Any = object : TransformableCoreFunctionDeclaration(parent, "print", "fun print(Any)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val valueArgument = callExpression.valueArguments[0]
            val expression = if (valueArgument.type.reference == Core.Kt.C_String) {
                valueArgument
            } else {
                EStringTemplateExpression(
                    callExpression.location,
                    listOf(ExpressionStringEntry(callExpression.valueArguments[0]))
                )
            }
            return ECallExpression(
                callExpression.location,
                Core.Kt.C_Unit.toType(),
                Target.F_write,
                null,
                arrayListOf(expression),
                ArrayList()
            )
        }
    }

    val F_print_Boolean = object : TransformableCoreFunctionDeclaration(parent, "print", "fun print(Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_print_Any.transform(callExpression)
        }
    }

    val F_print_Int = object : TransformableCoreFunctionDeclaration(parent, "print", "fun print(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_print_Any.transform(callExpression)
        }
    }

    val F_println = BasicCoreFunctionDeclaration(parent, "println", "fun println()", Target.F_display)

    val F_println_Any = object : TransformableCoreFunctionDeclaration(parent, "println", "fun println(Any)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val valueArgument = callExpression.valueArguments[0]
            val expression = if (valueArgument.type.reference == Core.Kt.C_String) {
                valueArgument
            } else {
                EStringTemplateExpression(
                    callExpression.location,
                    listOf(ExpressionStringEntry(callExpression.valueArguments[0]))
                )
            }
            return ECallExpression(
                callExpression.location,
                Core.Kt.C_Unit.toType(),
                Target.F_display,
                null,
                arrayListOf(expression),
                ArrayList()
            )
        }
    }

    val F_println_Boolean = object : TransformableCoreFunctionDeclaration(parent, "println", "fun println(Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_println_Any.transform(callExpression)
        }
    }

    val F_println_Int = object : TransformableCoreFunctionDeclaration(parent, "println", "fun println(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_println_Any.transform(callExpression)
        }
    }
}
