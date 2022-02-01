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
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor

abstract class EStringEntryExpression : EExpression(), ExpressionContainer {

    abstract val entries: List<StringEntry>

    override fun accept(visitor: Visitor) {
        visitor.visitStringEntryExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        entries.forEach {
            if (it is ExpressionStringEntry)
                it.expression.accept(visitor)
        }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        entries.forEach {
            if (it is ExpressionStringEntry && it.expression == oldExpression) {
                it.expression = newExpression
                return true
            }
        }
        return false
    }
}
