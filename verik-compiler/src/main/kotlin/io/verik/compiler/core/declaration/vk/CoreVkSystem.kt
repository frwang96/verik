/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

/**
 * Core system functions from the Verik core package.
 */
object CoreVkSystem : CoreScope(CorePackage.VK) {

    val F_strobe_String = BasicCoreFunctionDeclaration(parent, "strobe", "fun strobe(String)", Target.F_strobe)

    val F_monitor_String = BasicCoreFunctionDeclaration(parent, "monitor", "fun monitor(String)", Target.F_monitor)

    val F_finish = BasicCoreFunctionDeclaration(parent, "finish", "fun finish()", Target.F_finish)

    val F_fatal = BasicCoreFunctionDeclaration(parent, "fatal", "fun fatal()", Target.F_fatal)

    val F_fatal_String = object : TransformableCoreFunctionDeclaration(parent, "fatal", "fun fatal(String)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return ECallExpression(
                callExpression.location,
                callExpression.type,
                Target.F_fatal,
                null,
                false,
                arrayListOf(
                    EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "1"),
                    callExpression.valueArguments[0]
                ),
                ArrayList()
            )
        }
    }

    val F_error_String = BasicCoreFunctionDeclaration(parent, "error", "fun error(String)", Target.F_error)

    val F_warning_String = BasicCoreFunctionDeclaration(parent, "warning", "fun warning(String)", Target.F_warning)

    val F_info_String = BasicCoreFunctionDeclaration(parent, "info", "fun info(String)", Target.F_info)

    val F_time = BasicCoreFunctionDeclaration(parent, "time", "fun time()", Target.F_time)
}
