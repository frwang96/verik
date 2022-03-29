/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

/**
 * Core declarations from AssociativeArray.
 */
object CoreVkAssociativeArray : CoreScope(Core.Vk.C_AssociativeArray) {

    val F_set_K_V = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(K, V)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_set_Int_E.transform(callExpression)
        }
    }

    val F_get_K = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(K)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_get_Int.transform(callExpression)
        }
    }
}
