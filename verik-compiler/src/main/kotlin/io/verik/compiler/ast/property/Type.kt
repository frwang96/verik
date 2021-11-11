/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.ast.property

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.common.CardinalConstantDeclaration
import io.verik.compiler.common.CardinalDeclaration
import io.verik.compiler.common.CardinalUnresolvedDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.TargetClassDeclaration

class Type(
    override var reference: Declaration,
    var arguments: ArrayList<Type>
) : Reference {

    fun copy(): Type {
        val copyArguments = arguments.map { it.copy() }
        return Type(reference, ArrayList(copyArguments))
    }

    fun getArgument(indices: List<Int>): Type {
        return if (indices.isEmpty()) {
            this
        } else {
            arguments[indices[0]].getArgument(indices.drop(1))
        }
    }

    fun isSubtype(type: Type): Boolean {
        return getSuperTypes().any { it.reference == type.reference }
    }

    fun isCardinalType(): Boolean {
        return when (val reference = reference) {
            is ETypeParameter -> reference.type.isCardinalType()
            is ETypeAlias -> reference.type.isCardinalType()
            is CardinalDeclaration -> true
            else -> false
        }
    }

    fun isResolved(): Boolean {
        if (reference is CardinalUnresolvedDeclaration)
            return false
        return arguments.all { it.isResolved() }
    }

    fun isSpecialized(): Boolean {
        if (isCardinalType() && reference !is CardinalConstantDeclaration)
            return false
        return arguments.all { it.isSpecialized() }
    }

    fun asCardinalValue(element: EElement): Int {
        val reference = reference
        return if (reference is CardinalConstantDeclaration) {
            reference.value
        } else {
            Messages.INTERNAL_ERROR.on(element, "Could not get value as cardinal: $this")
            1
        }
    }

    fun asBitWidth(element: EElement): Int {
        return when (reference) {
            Core.Vk.C_Ubit -> arguments[0].asCardinalValue(element)
            Core.Vk.C_Sbit -> arguments[0].asCardinalValue(element)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Bit type expected: $this")
                1
            }
        }
    }

    fun asBitSigned(element: EElement): Boolean {
        return when (reference) {
            Core.Vk.C_Ubit -> false
            Core.Vk.C_Sbit -> true
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Bit type expected: $this")
                false
            }
        }
    }

    fun hasUnpackedDimension(element: EElement): Boolean {
        val reference = reference
        return if (reference is TargetClassDeclaration) {
            reference.serializeType(arguments, element).unpackedDimension != null
        } else false
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

    private fun getSuperTypes(): List<Type> {
        val superTypes = ArrayList<Type>()
        var superType: Type? = this
        while (superType != null) {
            superTypes.add(superType)
            superType = superType.getSuperType()
        }
        return superTypes.reversed()
    }

    private fun getSuperType(): Type? {
        return when (val reference = reference) {
            is EAbstractClass -> reference.superType
            is CoreClassDeclaration -> reference.superClass?.toType()
            is CardinalDeclaration -> null
            else -> throw IllegalArgumentException("Unexpected type reference: $reference")
        }
    }
}
