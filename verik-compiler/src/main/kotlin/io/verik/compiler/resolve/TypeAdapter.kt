/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.expression.common.ECallExpression

/**
 * An adapter that references a type in the AST.
 */
sealed class TypeAdapter {

    abstract fun getElement(): EElement

    abstract fun getType(): Type

    abstract fun setType(type: Type)

    abstract fun getFullType(): Type

    abstract fun substituteFullType(type: Type): Type

    companion object {

        fun ofElement(element: ETypedElement, vararg indices: Int): TypeAdapter {
            return ElementTypeAdapter(element, indices.toList())
        }

        fun ofTypeArgument(callExpression: ECallExpression, index: Int): TypeAdapter {
            return TypeArgumentTypeAdapter(callExpression, index)
        }
    }
}

/**
 * A type adapter that references the type of a typed element.
 */
class ElementTypeAdapter(
    private val typedElement: ETypedElement,
    private val indices: List<Int>
) : TypeAdapter() {

    override fun getElement(): EElement {
        return typedElement
    }

    override fun getType(): Type {
        return typedElement.type.getArgument(indices)
    }

    override fun setType(type: Type) {
        if (indices.isEmpty()) {
            typedElement.type = type
        } else {
            val parentType = typedElement.type.getArgument(indices.dropLast(1))
            parentType.arguments[indices.last()] = type
        }
    }

    override fun getFullType(): Type {
        return typedElement.type
    }

    override fun substituteFullType(type: Type): Type {
        return if (indices.isEmpty()) {
            type
        } else {
            val substitutedType = typedElement.type.copy()
            val parentType = substitutedType.getArgument(indices.dropLast(1))
            parentType.arguments[indices.last()] = type
            substitutedType
        }
    }
}

/**
 * A type adapter that references the type of a call expression type argument.
 */
class TypeArgumentTypeAdapter(
    private val callExpression: ECallExpression,
    private val index: Int
) : TypeAdapter() {

    override fun getElement(): EElement {
        return callExpression
    }

    override fun getType(): Type {
        return callExpression.typeArguments[index]
    }

    override fun setType(type: Type) {
        callExpression.typeArguments[index] = type
    }

    override fun getFullType(): Type {
        return callExpression.typeArguments[index]
    }

    override fun substituteFullType(type: Type): Type {
        return type
    }
}
