/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

enum class SignatureFragmentKind {
    TEXT,
    NULL,
    NAME,
    BREAK,
    INDENT_IN,
    INDENT_OUT,
    SEMICOLON,
    COLON,
    COLON_COLON,
    SHARP,
    COMMA,
    COMMA_BREAK,
    LBRACK,
    RBRACK,
    LPAREN,
    LPAREN_BREAK,
    RPAREN,
    RPAREN_BREAK,
    LBRACE,
    LBRACE_BREAK,
    RBRACE,
    RBRACE_BREAK
}
