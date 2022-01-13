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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EEventControlExpression(
    override val location: SourceLocation,
    var expressions: ArrayList<EExpression>
) : EExpression(), ExpressionContainer {

    override var type = Target.C_Void.toType()

    override val serializationType = SerializationType.EXPRESSION

    init {
        expressions.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEventControlExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expressions.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return expressions.replaceIfContains(oldExpression, newExpression)
    }
}
