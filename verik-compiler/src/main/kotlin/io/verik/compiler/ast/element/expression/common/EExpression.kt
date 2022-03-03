/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionKind
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SerializationKind

abstract class EExpression : ETypedElement() {

    abstract val serializationKind: SerializationKind

    open fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return true
    }

    fun replace(expression: EExpression) {
        parentNotNull().replaceChildAsExpressionContainer(this, expression)
    }

    // TODO more robust mechanism for differentiating statements and expressions
    fun getExpressionKind(): ExpressionKind {
        return when (val parent = this.parent) {
            is EProperty -> ExpressionKind.DIRECT_TYPED_SUBEXPRESSION
            is EBlockExpression -> {
                val parentParent = parent.parent
                if (parentParent is EIfExpression && parent.statements.last() == this) {
                    parentParent.getExpressionKind()
                } else ExpressionKind.STATEMENT
            }
            is EKtBinaryExpression -> {
                if (parent.kind == KtBinaryOperatorKind.EQ) ExpressionKind.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION
            }
            is ECallExpression -> {
                if (this in parent.valueArguments) ExpressionKind.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION
            }
            is EIfExpression -> {
                when {
                    this == parent.condition -> ExpressionKind.DIRECT_TYPED_SUBEXPRESSION
                    parent.getExpressionKind().isSubexpression() -> ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION
                    else -> ExpressionKind.STATEMENT
                }
            }
            is EWhenExpression -> {
                if (parent.getExpressionKind().isSubexpression()) ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION
                else ExpressionKind.STATEMENT
            }
            else -> ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION
        }
    }
}
