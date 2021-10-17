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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.Type

sealed class TypeAdapter {

    abstract fun getElement(): EElement

    abstract fun getType(): Type

    abstract fun setType(type: Type)

    abstract fun substitute(type: Type): SubstitutionResult

    class SubstitutionResult(val original: Type, val substituted: Type)

    companion object {

        fun ofElement(element: ETypedElement, vararg indices: Int): TypeAdapter {
            return ElementTypeAdapter(element, indices.toList())
        }

        fun ofElement(element: ETypedElement, indices: List<Int>): TypeAdapter {
            return ElementTypeAdapter(element, indices)
        }

        fun ofTypeArgument(callExpression: EKtCallExpression, index: Int): TypeAdapter {
            return TypeArgumentTypeAdapter(callExpression, index)
        }
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

    override fun substitute(type: Type): SubstitutionResult {
        return if (indices.isEmpty()) {
            SubstitutionResult(typedElement.type, type)
        } else {
            val substituted = typedElement.type.copy()
            val parentType = substituted.getArgument(indices.dropLast(1))
            parentType.arguments[indices.last()] = type
            SubstitutionResult(typedElement.type, substituted)
        }
    }
}

class TypeArgumentTypeAdapter(
    private val callExpression: EKtCallExpression,
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

    override fun substitute(type: Type): SubstitutionResult {
        return SubstitutionResult(callExpression.typeArguments[index], type)
    }
}
