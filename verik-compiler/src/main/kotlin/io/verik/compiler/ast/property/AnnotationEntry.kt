/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

/**
 * Annotation entry that represents a Kotlin annotation.
 */
class AnnotationEntry(
    val qualifiedName: String
) {

    val name = qualifiedName.substringAfterLast(".")

    override fun equals(other: Any?): Boolean {
        return other is AnnotationEntry && other.qualifiedName == qualifiedName
    }

    override fun hashCode(): Int {
        return qualifiedName.hashCode()
    }

    override fun toString(): String {
        return name
    }
}
