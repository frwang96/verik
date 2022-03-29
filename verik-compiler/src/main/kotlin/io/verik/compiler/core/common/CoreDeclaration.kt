/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.common.Declaration

/**
 * The base class of all core declarations. Core declarations are declarations defined in the Kotlin standard library or
 * the Verik core package that are supported by the compiler.
 */
interface CoreDeclaration : Declaration {

    val parent: CoreDeclaration?

    val signature: String

    fun getQualifiedSignature(): QualifiedSignature {
        return QualifiedSignature(getQualifiedName(), signature)
    }

    private fun getQualifiedName(): String {
        val parent = parent
        return if (parent != null) {
            "${parent.getQualifiedName()}.$name"
        } else name
    }
}
