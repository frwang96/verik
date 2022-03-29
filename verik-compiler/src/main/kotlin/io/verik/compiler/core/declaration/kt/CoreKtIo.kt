/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

/**
 * Core functions from the Kotlin IO package.
 */
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
                false,
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

    val F_print_Double = object : TransformableCoreFunctionDeclaration(parent, "print", "fun print(Double)") {

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
                false,
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

    val F_println_Double = object : TransformableCoreFunctionDeclaration(parent, "println", "fun println(Double)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_println_Any.transform(callExpression)
        }
    }
}
