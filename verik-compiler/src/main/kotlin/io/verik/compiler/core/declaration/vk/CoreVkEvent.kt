/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

/**
 * Core declarations from Event.
 */
object CoreVkEvent : CoreScope(Core.Vk.C_Event) {

    val F_trigger = object : TransformableCoreFunctionDeclaration(parent, "trigger", "fun trigger()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvUnaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                SvUnaryOperatorKind.TRIGGER
            )
        }
    }
}
