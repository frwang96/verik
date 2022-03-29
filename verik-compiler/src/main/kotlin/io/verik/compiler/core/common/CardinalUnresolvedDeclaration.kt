/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

/**
 * A cardinal declaration that represents a cardinal that has yet to be resolved.
 */
object CardinalUnresolvedDeclaration : CardinalDeclaration {

    override var name = "`*`"
}
