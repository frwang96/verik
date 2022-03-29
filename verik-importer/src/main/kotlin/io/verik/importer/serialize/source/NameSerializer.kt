/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.source

import io.verik.importer.ast.element.declaration.EDeclaration

/**
 * Serializer that serializes Kotlin names. Keywords are delimited by back ticks.
 */
object NameSerializer {

    private val KEYWORDS: List<String> = listOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while"
    )

    fun serializeName(declaration: EDeclaration, serializeContext: SerializeContext) {
        val name = declaration.name
        if (name in KEYWORDS) {
            serializeContext.append("`$name`")
        } else {
            serializeContext.append(name)
        }
    }
}
