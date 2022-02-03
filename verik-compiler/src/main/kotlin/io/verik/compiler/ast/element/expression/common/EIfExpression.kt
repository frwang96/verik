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

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EIfExpression(
    override val location: SourceLocation,
    override var type: Type,
    var condition: EExpression,
    var thenExpression: EBlockExpression?,
    var elseExpression: EBlockExpression?
) : EExpression(), ExpressionContainer {

    override val serializationType = SerializationType.STATEMENT

    init {
        condition.parent = this
        thenExpression?.parent = this
        elseExpression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitIfExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        thenExpression?.accept(visitor)
        elseExpression?.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return (blockExpression == condition)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        @Suppress("DuplicatedCode")
        newExpression.parent = this
        return when (oldExpression) {
            condition -> {
                condition = newExpression
                true
            }
            thenExpression -> {
                thenExpression = newExpression.cast()
                true
            }
            elseExpression -> {
                elseExpression = newExpression.cast()
                true
            }
            else -> false
        }
    }
}
