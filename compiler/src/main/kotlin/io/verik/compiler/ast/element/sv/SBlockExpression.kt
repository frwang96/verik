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

import io.verik.compiler.ast.element.common.CAbstractBlockExpression
import io.verik.compiler.ast.element.common.CExpression
import io.verik.compiler.ast.property.Name
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m

class SBlockExpression (
    override val location: SourceLocation,
    override val statements: ArrayList<CExpression>,
    val decorated: Boolean,
    val name: Name?
) : CAbstractBlockExpression() {

    init {
        statements.forEach { it.parent = this }
    }

    override var type = CoreClass.Kotlin.UNIT.toNoArgumentsType()

    override fun accept(visitor: Visitor) {
        visitor.visitSBlockExpression(this)
    }

    override fun copy(): SBlockExpression? {
        m.error("Unable to copy ${this::class.simpleName}", this)
        return null
    }
}
