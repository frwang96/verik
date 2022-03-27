/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.common.Declaration

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
