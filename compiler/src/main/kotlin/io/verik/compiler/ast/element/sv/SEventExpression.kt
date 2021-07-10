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

import io.verik.compiler.ast.element.common.CAbstractExpressionContainer
import io.verik.compiler.ast.element.common.CExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.SourceLocation

class SEventExpression(
    override val location: SourceLocation,
    override var expression: CExpression,
    var edgeType: EdgeType
) : CAbstractExpressionContainer() {

    override var type = CoreClass.Core.EVENT.toNoArgumentsType()

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSEventExpression(this)
    }
}