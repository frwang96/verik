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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class ESvUnaryExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var expression: EExpression,
    val kind: SvUnaryOperatorKind
) : EAbstractContainerExpression() {

    override val serializationType = SerializationType.EXPRESSION

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvUnaryExpression(this)
    }
}
