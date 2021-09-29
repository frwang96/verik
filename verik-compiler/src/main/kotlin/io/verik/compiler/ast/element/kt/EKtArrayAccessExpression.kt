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

import io.verik.compiler.ast.element.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

class EKtArrayAccessExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var array: EExpression,
    var indices: ArrayList<EExpression>
) : EAbstractArrayAccessExpression() {

    override val serializationType = SvSerializationType.INTERNAL

    init {
        array.parent = this
        indices.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtArrayAccessExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        indices.forEach { it.accept(visitor) }
    }

    override fun copy(): EKtArrayAccessExpression {
        val typeCopy = type.copy()
        val arrayCopy = array.copy()
        val indicesCopy = indices.map { it.copy() }
        return EKtArrayAccessExpression(location, typeCopy, arrayCopy, ArrayList(indicesCopy))
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression) {
        newExpression.parent = this
        if (array == oldExpression) {
            array = newExpression
            return
        } else if (!indices.replaceIfContains(oldExpression, newExpression)) {
            Messages.INTERNAL_ERROR.on(this, "Could not find $oldExpression in $this")
        }
    }
}