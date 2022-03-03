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
import io.verik.compiler.ast.element.expression.common.EAbstractBinaryExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class ESvBinaryExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var left: EExpression,
    override var right: EExpression,
    var kind: SvBinaryOperatorKind
) : EAbstractBinaryExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        left.parent = this
        right.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvBinaryExpression(this)
    }
}
