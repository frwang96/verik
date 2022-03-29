/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.common.Declaration

/**
 * A null declaration that is used to represent declarations that have yet to be resolved.
 */
object NullDeclaration : Declaration {

    override var name = "null"
}
