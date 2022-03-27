/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.core.CardinalConstantDeclaration
import io.verik.importer.core.Core

class Type(
    var reference: Declaration,
    var arguments: ArrayList<Type>
) {

    fun copy(): Type {
        val copyArguments = arguments.map { it.copy() }
        return Type(reference, ArrayList(copyArguments))
    }

    fun isResolved(): Boolean {
        return if (arguments.any { !it.isResolved() }) {
            false
        } else {
            reference != Core.C_Nothing
        }
    }

    fun isNullable(): Boolean {
        return when (val reference = reference) {
            is ETypeAlias -> reference.descriptor.type.isNullable()
            is EKtClass -> reference.superDescriptor.type.isNullable()
            Core.C_Class -> true
            else -> false
        }
    }

    fun asCardinalValueOrNull(): Int? {
        val reference = reference
        return if (reference is CardinalConstantDeclaration) reference.value
        else null
    }

    override fun toString(): String {
        val referenceName = reference.name
        return if (arguments.isNotEmpty()) {
            "$referenceName<${arguments.joinToString()}>"
        } else referenceName
    }

    override fun equals(other: Any?): Boolean {
        return (other is Type) && (other.reference == reference) && (other.arguments == arguments)
    }

    override fun hashCode(): Int {
        var result = reference.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }

    companion object {

        fun unresolved(): Type {
            return Core.C_Nothing.toType()
        }
    }
}
