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
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.TypeVisitor
import io.verik.compiler.core.CoreCardinalBaseDeclaration
import io.verik.compiler.core.CoreCardinalDeclaration
import io.verik.compiler.core.CoreClassDeclaration
import io.verik.compiler.main.m

class Type(
    override var reference: Declaration,
    val arguments: ArrayList<Type>
) : Reference {

    fun isCardinalType(): Boolean {
        return when (val reference = reference) {
            is ETypeParameter -> reference.type.isCardinalType()
            is CoreCardinalDeclaration -> true
            else -> false
        }
    }

    fun isType(type: Type): Boolean {
        return getSupertypes().any { it == type }
    }

    fun copy(): Type {
        val copyArguments = arguments.map { it.copy() }
        return Type(reference, ArrayList(copyArguments))
    }

    fun accept(typeVisitor: TypeVisitor) {
        typeVisitor.visit(this)
    }

    override fun toString(): String {
        return if (arguments.isNotEmpty()) {
            "$reference<${arguments.joinToString()}>"
        } else "$reference"
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
            is CoreClassDeclaration -> reference.superclass?.toNoArgumentsType()
            is CoreCardinalBaseDeclaration -> null
            else -> {
                m.error("Unexpected type reference: $reference", null)
                return null
            }
        }
    }

    companion object {

        val NULL = NullDeclaration.toNoArgumentsType()
    }
}