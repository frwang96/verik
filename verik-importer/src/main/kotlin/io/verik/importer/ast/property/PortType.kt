/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.property

/**
 * Enum for port type.
 */
enum class PortType {
    INPUT,
    OUTPUT;

    fun getAnnotationEntry(): AnnotationEntry {
        return when (this) {
            INPUT -> AnnotationEntry("In")
            OUTPUT -> AnnotationEntry("Out")
        }
    }
}
