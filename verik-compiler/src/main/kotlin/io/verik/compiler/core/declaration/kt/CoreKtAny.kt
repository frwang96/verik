/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope

object CoreKtAny : CoreScope(Core.Kt.C_Any) {

    val F_toString = BasicCoreFunctionDeclaration(parent, "toString", "fun toString()", null)
}
