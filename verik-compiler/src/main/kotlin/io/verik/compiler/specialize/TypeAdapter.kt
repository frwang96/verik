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
}

class TypedElementTypeAdapter(
    private val typedElement: ETypedElement
) : TypeAdapter() {

    override fun getLocation(): SourceLocation {
        return typedElement.location
    }

    override fun getType(): Type {
        return typedElement.type
    }

    override fun setType(type: Type) {
        typedElement.type = type
    }
}

class TypedElementTypeArgumentTypeAdapter(
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
}

class CallExpressionTypeArgumentTypeAdapter(
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
}
