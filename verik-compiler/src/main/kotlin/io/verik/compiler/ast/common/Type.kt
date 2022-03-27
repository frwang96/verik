/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.CardinalConstantDeclaration
import io.verik.compiler.core.common.CardinalDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetClassDeclaration

class Type(
    var reference: Declaration,
    var arguments: ArrayList<Type>
) {

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

    fun getSuperTypes(): List<Type> {
        val superTypes = ArrayList<Type>()
        var superType: Type? = this
        while (superType != null) {
            superTypes.add(superType)
            superType = superType.getSuperType()
        }
        return superTypes
    }

    fun isSubtype(type: Type): Boolean {
        return getSuperTypes().any { it.reference == type.reference }
    }

    fun isSubtype(declaration: Declaration): Boolean {
        return isSubtype(declaration.toType())
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
        if (isCardinalType() && reference !is CardinalConstantDeclaration)
            return false
        return arguments.all { it.isResolved() }
    }

    fun asCardinalValue(element: EElement): Int {
        val reference = reference
        return if (reference is CardinalConstantDeclaration) {
            reference.value
        } else {
            Messages.INTERNAL_ERROR.on(element, "Could not get value as cardinal: $this")
        }
    }

    fun asBitWidth(element: EElement): Int {
        return when (reference) {
            Core.Vk.C_Ubit -> arguments[0].asCardinalValue(element)
            Core.Vk.C_Sbit -> arguments[0].asCardinalValue(element)
            Target.C_Ubit -> arguments[0].asCardinalValue(element)
            Target.C_Sbit -> arguments[0].asCardinalValue(element)
            else -> Messages.INTERNAL_ERROR.on(element, "Bit type expected: $this")
        }
    }

    fun asBitSigned(element: EElement): Boolean {
        return when (reference) {
            Core.Vk.C_Ubit -> false
            Core.Vk.C_Sbit -> true
            else -> Messages.INTERNAL_ERROR.on(element, "Bit type expected: $this")
        }
    }

    fun getWidthAsType(element: EElement): Type {
        return when (reference) {
            Core.Kt.C_Boolean -> Cardinal.of(1).toType()
            Core.Vk.C_Ubit, Core.Vk.C_Sbit -> arguments[0].copy()
            else -> {
                Messages.TYPE_NO_WIDTH.on(element, this)
                Cardinal.of(0).toType()
            }
        }
    }

    fun getWidthAsInt(element: EElement): Int {
        return when (reference) {
            Core.Kt.C_Boolean -> 1
            Core.Vk.C_Ubit, Core.Vk.C_Sbit -> asBitWidth(element)
            else -> {
                Messages.TYPE_NO_WIDTH.on(element, this)
                0
            }
        }
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

    private fun getSuperType(): Type? {
        return when (val reference = reference) {
            is ETypeAlias -> reference.type.getSuperType()
            is ETypeParameter -> reference.type.getSuperType()
            is EKtClass -> {
                val type = reference.superType.copy()
                type.substituteTypeParameters(reference.typeParameters, arguments)
                type
            }
            is EAbstractClass -> reference.superType.copy()
            is CoreClassDeclaration -> reference.superClass?.toType()
            is TargetClassDeclaration -> null
            is CardinalDeclaration -> null
            else -> Messages.INTERNAL_ERROR.on(
                SourceLocation.NULL,
                "Unexpected type reference: ${reference::class.simpleName}"
            )
        }
    }

    private fun substituteTypeParameters(
        typeParameters: List<ETypeParameter>,
        typeArguments: List<Type>
    ) {
        val reference = reference
        if (reference is ETypeParameter) {
            val index = typeParameters.indexOf(reference)
            if (index == -1) {
                Messages.INTERNAL_ERROR.on(
                    SourceLocation.NULL,
                    "Unable to substitute type parameter: ${reference.name}"
                )
            }
            val type = typeArguments[index].copy()
            this.reference = type.reference
            arguments = type.arguments
        } else {
            arguments.forEach { it.substituteTypeParameters(typeParameters, typeArguments) }
        }
    }
}
