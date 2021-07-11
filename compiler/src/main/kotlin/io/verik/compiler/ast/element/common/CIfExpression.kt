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

import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m

class CIfExpression(
    override val location: SourceLocation,
    override var type: Type,
    var condition: CExpression,
    var thenExpression: CExpression?,
    var elseExpression: CExpression?
) : CExpression(), ExpressionContainer {

    init {
        condition.parent = this
        thenExpression?.parent = this
        elseExpression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCIfExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        thenExpression?.accept(visitor)
        elseExpression?.accept(visitor)
    }

    override fun replaceChild(oldExpression: CExpression, newExpression: CExpression) {
        newExpression.parent = this
        when (oldExpression) {
            condition -> condition = newExpression
            thenExpression -> thenExpression = newExpression
            elseExpression -> elseExpression = newExpression
            else -> m.error("Could not find ${oldExpression::class.simpleName} in ${this::class.simpleName}", this)
        }
    }

    override fun copy(): CExpression? {
        val copyCondition = condition.copy() ?: return null
        val copyThenExpression = thenExpression?.let { it.copy() ?: return null }
        val copyElseExpression = elseExpression?.let { it.copy() ?: return null }
        return CIfExpression(location, type, copyCondition, copyThenExpression, copyElseExpression)
    }
}