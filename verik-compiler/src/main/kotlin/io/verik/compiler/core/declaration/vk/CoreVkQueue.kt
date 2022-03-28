/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

object CoreVkQueue : CoreScope(Core.Vk.C_Queue) {

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return ECallExpression(
                referenceExpression.location,
                referenceExpression.type,
                Target.Queue.F_size,
                referenceExpression.receiver,
                false,
                ArrayList(),
                ArrayList()
            )
        }
    }

    val F_add_E = BasicCoreFunctionDeclaration(parent, "add", "fun add(E)", Target.Queue.F_push_back)

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_get_Int.transform(callExpression)
        }
    }
}
