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

package io.verik.compiler.ast.element.kt

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

class EWhenExpression(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var type: Type,
    var subject: EExpression?,
    var entries: List<WhenEntry>
) : EExpression(), ExpressionContainer {

    override val serializationType = SerializationType.INTERNAL

    init {
        subject?.parent = this
        entries.forEach { entry ->
            entry.conditions.forEach { it.parent = this }
            entry.body.parent = this
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitWhenExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        subject?.accept(visitor)
        entries.forEach { entry ->
            entry.conditions.forEach { it.accept(visitor) }
            entry.body.accept(visitor)
        }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        if (subject == oldExpression) {
            subject = newExpression
            return true
        }
        entries.forEach { entry ->
            if (entry.conditions.replaceIfContains(oldExpression, newExpression))
                return true
            if (entry.body == oldExpression) {
                entry.body = newExpression.cast()
                return true
            }
        }
        return false
    }
}
