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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionType
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SerializationType

abstract class EExpression : ETypedElement() {

    abstract val serializationType: SerializationType

    open fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return true
    }

    fun replace(expression: EExpression) {
        parentNotNull().replaceChildAsExpressionContainer(this, expression)
    }

    // TODO more robust mechanism for differentiating statements and expressions
    fun getExpressionType(): ExpressionType {
        return when (val parent = this.parent) {
            is EProperty -> ExpressionType.DIRECT_TYPED_SUBEXPRESSION
            is EBlockExpression -> {
                val parentParent = parent.parent
                if (parentParent is EIfExpression && parent.statements.last() == this) {
                    parentParent.getExpressionType()
                } else ExpressionType.STATEMENT
            }
            is EKtBinaryExpression -> {
                if (parent.kind == KtBinaryOperatorKind.EQ) ExpressionType.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
            }
            is ECallExpression -> {
                if (this in parent.valueArguments) ExpressionType.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
            }
            is EIfExpression -> {
                when {
                    this == parent.condition -> ExpressionType.DIRECT_TYPED_SUBEXPRESSION
                    parent.getExpressionType().isSubexpression() -> ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
                    else -> ExpressionType.STATEMENT
                }
            }
            is EWhenExpression -> {
                if (parent.getExpressionType().isSubexpression()) ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.STATEMENT
            }
            else -> ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
        }
    }
}
