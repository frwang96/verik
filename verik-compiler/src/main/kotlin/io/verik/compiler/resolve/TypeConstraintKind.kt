/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

/**
 * Type constraint kind. IN / OUT is whether the type under consideration is inward or outwards and is used to
 * generate expected type and actual type in error messages.
 */
enum class TypeConstraintKind {
    EQ_IN,
    EQ_OUT,
    EQ_INOUT,
    LOG_IN,
    WIDTH_OUT,
    MAX_OUT,
    MAX_INC_OUT,
    ADD_OUT,
    CAT_OUT,
    REP_OUT,
    EXT_IN,
    TRU_IN
}
