/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages

object CoreVkCoverPoint : CoreScope(Core.Vk.C_CoverPoint) {

    val F_bin_String_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "bin",
        "fun bin(String, String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_bins_String_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "bins",
        "fun bins(String, String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_bin_String_String.transform(callExpression)
        }
    }

    val F_ignoreBin_String_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "ignoreBin",
        "fun ignoreBin(String, String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_bin_String_String.transform(callExpression)
        }
    }
}
