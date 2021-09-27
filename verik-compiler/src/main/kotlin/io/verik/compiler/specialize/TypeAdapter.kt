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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.message.SourceLocation

sealed class TypeAdapter {

    abstract fun getLocation(): SourceLocation

    abstract fun getType(): Type

    abstract fun setType(type: Type)

    abstract fun getCheckerResult(type: Type): CheckerResult

    class CheckerResult(val actual: Type, val expected: Type)

    companion object {

        fun ofElement(element: ETypedElement, vararg indices: Int): TypeAdapter {
            return ElementTypeAdapter(element, indices.toList())
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

    override fun getLocation(): SourceLocation {
        return typedElement.location
    }

    override fun getType(): Type {
        var type = typedElement.type
        indices.forEach { type = type.arguments[it] }
        return type
    }

    override fun setType(type: Type) {
        if (indices.isEmpty()) {
            typedElement.type = type
        } else {
            var currentType = typedElement.type
            indices.dropLast(1).forEach { currentType = currentType.arguments[it] }
            currentType.arguments[indices.last()] = type
        }
    }

    override fun getCheckerResult(type: Type): CheckerResult {
        return if (indices.isEmpty()) {
            CheckerResult(typedElement.type, type)
        } else {
            val expectedType = typedElement.type.copy()
            var expectedTypeArgument = expectedType
            indices.dropLast(1).forEach { expectedTypeArgument = expectedTypeArgument.arguments[it] }
            expectedTypeArgument.arguments[indices.last()] = type
            CheckerResult(typedElement.type, expectedType)
        }
    }
}

class TypeArgumentTypeAdapter(
    private val callExpression: EKtCallExpression,
    private val index: Int
) : TypeAdapter() {

    override fun getLocation(): SourceLocation {
        return callExpression.location
    }

    override fun getType(): Type {
        return callExpression.typeArguments[index]
    }

    override fun setType(type: Type) {
        callExpression.typeArguments[index] = type
    }

    override fun getCheckerResult(type: Type): CheckerResult {
        return CheckerResult(callExpression.typeArguments[index], type)
    }
}
