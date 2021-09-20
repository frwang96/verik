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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.property.Type

sealed class TypeConstraint

// TODO general way of setting indices of type arguments
class TypeArgumentTypeConstraint(
    val callExpression: EKtCallExpression,
    private val returnTypeArgumentIndices: List<Int>
) : TypeConstraint() {

    fun getTypeArgument(): Type {
        var type = callExpression.type
        returnTypeArgumentIndices.forEach { type = type.arguments[it] }
        return type
    }

    fun setTypeArgument(type: Type) {
        if (returnTypeArgumentIndices.isEmpty()) {
            callExpression.type = type
        } else {
            var currentType = callExpression.type
            returnTypeArgumentIndices.dropLast(1).forEach {
                currentType = currentType.arguments[it]
            }
            currentType.arguments[returnTypeArgumentIndices.last()] = type
        }
    }
}

class ValueArgumentTypeConstraint(
    val valueArgument: EExpression,
    val valueParameter: EKtValueParameter
) : TypeConstraint()

class TypeEqualsTypeConstraint(
    val inner: ETypedElement,
    val outer: ETypedElement
) : TypeConstraint()

class BinaryOperatorTypeConstraint(
    val left: EExpression,
    val right: EExpression,
    val outer: EExpression,
    val kind: BinaryOperatorTypeConstraintKind
) : TypeConstraint()

class CardinalBitConstantTypeConstraint(
    val callExpression: EKtCallExpression
) : TypeConstraint()

class ConcatenationTypeConstraint(
    val callExpression: EKtCallExpression
) : TypeConstraint()
