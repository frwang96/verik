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

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.property.Type

sealed class TypeAdapter {

    abstract fun getElement(): EElement

    abstract fun getType(): Type

    abstract fun setType(type: Type)

    abstract fun getFullType(): Type

    abstract fun substituteFullType(type: Type): Type

    companion object {

        fun ofConstant(type: Type): TypeAdapter {
            return ConstantTypeAdapter(type)
        }

        fun ofElement(element: ETypedElement, vararg indices: Int): TypeAdapter {
            return ElementTypeAdapter(element, indices.toList())
        }

        fun ofTypeArgument(callExpression: ECallExpression, index: Int): TypeAdapter {
            return TypeArgumentTypeAdapter(callExpression, index)
        }
    }
}

class ConstantTypeAdapter(
    private val type: Type
) : TypeAdapter() {

    override fun getElement(): EElement {
        throw IllegalArgumentException("Cannot get element of constant type adapter")
    }

    override fun getType(): Type {
        return type
    }

    override fun setType(type: Type) {
        throw IllegalArgumentException("Cannot set type of constant type adapter")
    }

    override fun getFullType(): Type {
        return type
    }

    override fun substituteFullType(type: Type): Type {
        throw IllegalArgumentException("Cannot substitute type of constant type adapter")
    }
}

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
