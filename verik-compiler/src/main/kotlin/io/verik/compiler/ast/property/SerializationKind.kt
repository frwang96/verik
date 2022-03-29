/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

/**
 * Enum for serialization kind. The internal serialization kind is used for expressions that are used internally and
 * should not be serialized.
 */
enum class SerializationKind {
    EXPRESSION,
    STATEMENT,
    INTERNAL
}
