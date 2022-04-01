/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

/**
 * Enum for package kind. Regular packages are packages that are not imported.
 */
enum class PackageKind {
    REGULAR_NON_ROOT,
    REGULAR_ROOT,
    IMPORTED_NON_ROOT,
    IMPORTED_ROOT;

    fun isRoot(): Boolean {
        return this in listOf(REGULAR_ROOT, IMPORTED_ROOT)
    }

    fun isImported(): Boolean {
        return this in listOf(IMPORTED_NON_ROOT, IMPORTED_ROOT)
    }
}
