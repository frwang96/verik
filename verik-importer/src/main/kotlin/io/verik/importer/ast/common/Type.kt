/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            Core.C_Any -> true
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
