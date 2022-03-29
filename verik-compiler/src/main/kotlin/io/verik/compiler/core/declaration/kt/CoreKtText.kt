/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

/**
 * Core functions from the Kotlin text package.
 */
object CoreKtText : CoreScope(CorePackage.KT_TEXT) {

    val F_trimIndent = BasicCoreFunctionDeclaration(parent, "trimIndent", "fun trimIndent()", null)
}
