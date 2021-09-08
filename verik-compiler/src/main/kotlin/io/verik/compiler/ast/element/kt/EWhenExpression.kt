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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

class EWhenExpression(
    override val location: SourceLocation,
    override var type: Type,
    var subject: EExpression,
    val entries: List<WhenEntry>
) : EExpression(), ExpressionContainer {

    override val serializationType = SvSerializationType.OTHER

    init {
        subject.parent = this
        entries.forEach { entry ->
            entry.conditions.forEach { it.parent = this }
            entry.body.parent = this
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitWhenExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        subject.accept(visitor)
        entries.forEach { entry ->
            entry.conditions.forEach { it.accept(visitor) }
            entry.body.accept(visitor)
        }
    }

    override fun copy(): EWhenExpression {
        val typeCopy = type.copy()
        val subjectCopy = subject.copy()
        val entriesCopy = entries.map { it.copy() }
        return EWhenExpression(location, typeCopy, subjectCopy, entriesCopy)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression) {
        newExpression.parent = this
        if (subject == oldExpression) {
            subject = newExpression
            return
        }
        entries.forEach { entry ->
            if (entry.conditions.replaceIfContains(oldExpression, newExpression))
                return
            if (entry.body == oldExpression) {
                entry.body = newExpression
                return
            }
        }
        Messages.INTERNAL_ERROR.on(this, "Could not find $oldExpression in $this")
    }
}
