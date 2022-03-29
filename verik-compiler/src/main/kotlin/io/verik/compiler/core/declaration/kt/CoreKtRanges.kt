/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

/**
 * Core functions from the Kotlin ranges package.
 */
object CoreKtRanges : CoreScope(CorePackage.KT_RANGES) {

    val F_until_Int = BasicCoreFunctionDeclaration(parent, "until", "fun until(Int)", null)
}
