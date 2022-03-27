/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

enum class ExpressionKind {
    STATEMENT,
    DIRECT_TYPED_SUBEXPRESSION,
    INDIRECT_TYPED_SUBEXPRESSION;

    fun isSubexpression(): Boolean {
        return this != STATEMENT
    }
}
