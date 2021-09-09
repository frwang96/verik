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

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class ESvBlockExpression(
    override val location: SourceLocation,
    override val statements: ArrayList<EExpression>,
    val decorated: Boolean,
    val name: String?
) : EAbstractBlockExpression() {

    override val serializationType = SvSerializationType.STATEMENT

    init {
        statements.forEach { it.parent = this }
    }

    override var type = Core.Kt.C_UNIT.toType()

    override fun accept(visitor: Visitor) {
        visitor.visitSvBlockExpression(this)
    }

    override fun copy(): EExpression {
        val copyStatements = statements.map { it.copy() }
        return ESvBlockExpression(location, ArrayList(copyStatements), decorated, name)
    }
}
