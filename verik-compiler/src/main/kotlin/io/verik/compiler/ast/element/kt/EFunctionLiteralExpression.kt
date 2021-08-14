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
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m

class EFunctionLiteralExpression(
    override val location: SourceLocation,
    var body: EExpression
) : EExpression(), ExpressionContainer {

    override val serializationType = SvSerializationType.OTHER

    init {
        body.parent = this
    }

    override var type = Core.Kt.FUNCTION.toType()

    override fun accept(visitor: Visitor) {
        visitor.visitFunctionLiteralExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        body.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression) {
        newExpression.parent = this
        if (body == oldExpression)
            body = newExpression.cast()
                ?: return
        else
            m.error("Could not find $oldExpression in $this", this)
    }

    override fun copy(): EExpression {
        val copyBody = body.copy()
        return EFunctionLiteralExpression(location, copyBody)
    }
}