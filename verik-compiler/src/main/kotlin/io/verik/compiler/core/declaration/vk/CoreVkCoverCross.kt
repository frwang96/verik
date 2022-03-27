/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

object CoreVkCoverCross : CoreScope(Core.Vk.C_CoverCross) {

    val F_bin_String_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "bin",
        "fun bin(String, String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkCoverPoint.F_bin_String_String.transform(callExpression)
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
