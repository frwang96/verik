/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

/**
 * Enum for expression kind. Direct typed subexpressions are for instance the RHS of an assignment or the value argument
 * of a call expression where the type can be inferred directly.
 */
enum class ExpressionKind {
    STATEMENT,
    DIRECT_TYPED_SUBEXPRESSION,
    INDIRECT_TYPED_SUBEXPRESSION;

    fun isSubexpression(): Boolean {
        return this != STATEMENT
    }
}
