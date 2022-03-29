/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

/**
 * Enum for the type of a source action. Soft and hard breaks indicate locations where the source code may wrap. If
 * the source code does not wrap, hard breaks insert a whitespace but soft breaks do not.
 */
enum class SourceActionType {
    REGULAR,
    SOFT_BREAK,
    HARD_BREAK,
    ALIGN
}
