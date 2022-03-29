/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

/**
 * Interface for declarations that can be referred to in types or reference descriptors.
 */
interface Declaration {

    val name: String

    fun toType(vararg arguments: Type): Type {
        return Type(this, arrayListOf(*arguments))
    }

    fun toType(arguments: List<Type>): Type {
        return Type(this, ArrayList(arguments))
    }
}
