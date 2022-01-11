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

package io.verik.compiler.ast.element.sv

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EImmediateAssertStatement(
    override val location: SourceLocation,
    override var type: Type,
    var condition: EExpression,
    var elseExpression: EAbstractBlockExpression?
) : EExpression(), ExpressionContainer {

    override val serializationType = SerializationType.STATEMENT

    init {
        condition.parent = this
        elseExpression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitImmediateAssertStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        elseExpression?.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EKtBlockExpression): Boolean {
        return (blockExpression == condition)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            condition -> {
                condition = newExpression
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
