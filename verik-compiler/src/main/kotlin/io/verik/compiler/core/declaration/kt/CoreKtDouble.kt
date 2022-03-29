/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope

/**
 * Core declarations from Double.
 */
object CoreKtDouble : CoreScope(Core.Kt.C_Double) {

    val F_plus_Int = BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Int)",
        SvBinaryOperatorKind.PLUS
    )

    val F_plus_Double = BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Double)",
        SvBinaryOperatorKind.PLUS
    )

    val F_div_Int = BinaryCoreFunctionDeclaration(
        parent,
        "div",
        "fun div(Int)",
        SvBinaryOperatorKind.DIV
    )
}
