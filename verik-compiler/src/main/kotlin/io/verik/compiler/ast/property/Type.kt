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
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.core.common.*
import io.verik.compiler.main.m

class Type(
    override var reference: Declaration,
    val arguments: ArrayList<Type>
) : Reference {

    fun isSubtype(type: Type): Boolean {
        return getSupertypes().any { it == type }
    }

    fun copy(): Type {
        val copyArguments = arguments.map { it.copy() }
        return Type(reference, ArrayList(copyArguments))
    }

    fun isCardinalType(): Boolean {
        return when (val reference = reference) {
            is ETypeParameter -> reference.typeConstraint.isCardinalType()
            is CoreCardinalDeclaration -> true
            else -> false
        }
    }

    fun isResolved(): Boolean {
        if (isCardinalType() && reference is CoreCardinalBaseDeclaration)
            return false
        return arguments.all { it.isResolved() }
    }

    fun isSpecialized(): Boolean {
        if (isCardinalType() && reference !is CoreCardinalConstantDeclaration)
            return false
        return arguments.all { it.isSpecialized() }
    }

    private fun asCardinalValueOrNull(): Int? {
        val reference = reference
        return if (reference is CoreCardinalConstantDeclaration) {
            reference.value
        } else null
    }

    private fun asCardinalValue(element: EElement): Int {
        val value = asCardinalValueOrNull()
        return if (value != null) {
            value
        } else {
            m.error("Could not get value as cardinal: $this", element)
            1
        }
    }

    fun asBitWidthOrNull(element: EElement): Int? {
        val reference = reference
        return if (reference in listOf(Core.Vk.UBIT, Core.Vk.SBIT)) {
            arguments[0].asCardinalValueOrNull()
        } else {
            m.error("Bit type expected: $this", element)
            1
        }
    }

    fun asBitWidth(element: EElement): Int {
        val reference = reference
        return if (reference in listOf(Core.Vk.UBIT, Core.Vk.SBIT)) {
            arguments[0].asCardinalValue(element)
        } else {
            m.error("Bit type expected: $this", element)
            1
        }
    }

    override fun toString(): String {
        val reference = reference
        val referenceName = if (reference is CoreCardinalDeclaration) reference.displayName()
        else reference.name
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

    private fun getSupertypes(): List<Type> {
        val supertypes = ArrayList<Type>()
        var supertype: Type? = this
        while (supertype != null) {
            supertypes.add(supertype)
            supertype = supertype.getSupertype()
        }
        return supertypes.reversed()
    }

    private fun getSupertype(): Type? {
        return when (val reference = reference) {
            is EAbstractClass -> reference.supertype
            is CoreClassDeclaration -> reference.superclass?.toType()
            is CoreCardinalBaseDeclaration -> null
            else -> {
                m.error("Unexpected type reference: $reference", null)
                return null
            }
        }
    }
}