/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.EImmediateAssertStatement
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

/**
 * Core declarations from Class.
 */
object CoreVkClass : CoreScope(Core.Vk.C_Class) {

    val F_randomize = object : TransformableCoreFunctionDeclaration(parent, "randomize", "fun randomize()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val copiedCallExpression = ExpressionCopier.shallowCopy(callExpression)
            copiedCallExpression.reference = Target.F_randomize
            return EImmediateAssertStatement(callExpression.location, copiedCallExpression, null)
        }
    }

    val F_preRandomize = BasicCoreFunctionDeclaration(
        parent,
        "preRandomize",
        "fun preRandomize()",
        Target.F_pre_randomize
    )

    val F_postRandomize = BasicCoreFunctionDeclaration(
        parent,
        "postRandomize",
        "fun postRandomize()",
        Target.F_post_randomize
    )
}
