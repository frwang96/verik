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

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EConstantPartSelectExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var array: EExpression,
    var startIndex: EExpression,
    var endIndex: EExpression
) : EAbstractArrayAccessExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        array.parent = this
        startIndex.parent = this
        endIndex.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitConstantPartSelectExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        startIndex.accept(visitor)
        endIndex.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        @Suppress("DuplicatedCode")
        newExpression.parent = this
        return when (oldExpression) {
            array -> {
                array = newExpression
                true
            }
            startIndex -> {
                startIndex = newExpression
                true
            }
            endIndex -> {
                endIndex = newExpression
                true
            }
            else -> false
        }
    }
}
