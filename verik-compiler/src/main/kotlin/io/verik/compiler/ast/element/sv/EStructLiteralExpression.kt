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
import io.verik.compiler.ast.property.StructLiteralEntry
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EStructLiteralExpression(
    override val location: SourceLocation,
    override var type: Type,
    val entries: List<StructLiteralEntry>
) : EExpression(), ExpressionContainer {

    override val serializationType = SvSerializationType.EXPRESSION

    init {
        entries.forEach { it.expression.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitStructLiteralExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        entries.forEach { it.expression.accept(visitor) }
    }

    override fun copy(): EExpression {
        val copyType = type.copy()
        val copyEntries = entries.map { it.copy() }
        return EStructLiteralExpression(location, copyType, copyEntries)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        entries.forEach {
            if (it.expression == oldExpression) {
                it.expression = newExpression
                return true
            }
        }
        return false
    }
}
