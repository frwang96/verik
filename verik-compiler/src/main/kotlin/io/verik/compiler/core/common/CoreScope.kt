/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

/**
 * A scope that contains core declarations. Core declarations defined in this scope belong to [parent].
 */
open class CoreScope(coreDeclaration: CoreDeclaration) {

    val parent = coreDeclaration
}
