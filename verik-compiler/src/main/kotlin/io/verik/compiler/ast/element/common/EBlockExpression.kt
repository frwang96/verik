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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EBlockExpression(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var type: Type,
    var statements: ArrayList<EExpression>
) : EExpression(), ExpressionContainer {

    override val serializationType = SerializationType.STATEMENT

    init {
        statements.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitBlockExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        statements.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return statements.replaceIfContains(oldExpression, newExpression)
    }

    companion object {

        fun empty(location: SourceLocation): EBlockExpression {
            return EBlockExpression(location, location, Core.Kt.C_Unit.toType(), ArrayList())
        }

        fun wrap(expression: EExpression): EBlockExpression {
            return if (expression !is EBlockExpression) {
                EBlockExpression(
                    expression.location,
                    expression.location,
                    expression.type.copy(),
                    arrayListOf(expression)
                )
            } else expression
        }

        fun extract(expression: EExpression, extractedExpressions: List<EExpression>) {
            val blockExpression = EBlockExpression(
                expression.location,
                expression.location,
                expression.type.copy(),
                ArrayList(extractedExpressions)
            )
            expression.replace(blockExpression)
        }
    }
}
