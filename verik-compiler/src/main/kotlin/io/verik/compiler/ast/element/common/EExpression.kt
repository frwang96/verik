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

    fun replace(expression: EExpression) {
        parentNotNull().replaceChildAsExpressionContainer(this, expression)
    }

    fun getExpressionType(): ExpressionType {
        return when (val parent = this.parent) {
            is EAbstractInitializedProperty -> ExpressionType.DIRECT_TYPED_SUBEXPRESSION
            is EAbstractBlockExpression -> ExpressionType.STATEMENT
            is EKtBinaryExpression -> {
                if (parent.kind == KtBinaryOperatorKind.EQ) ExpressionType.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
            }
            is EAbstractCallExpression -> {
                if (this in parent.valueArguments) ExpressionType.DIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
            }
            is EIfExpression -> {
                if (parent.getExpressionType().isSubexpression()) ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.STATEMENT
            }
            is EWhenExpression -> {
                if (parent.getExpressionType().isSubexpression()) ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
                else ExpressionType.STATEMENT
            }
            else -> ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
        }
    }
}
