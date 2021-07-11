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
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.C
import io.verik.compiler.main.SourceLocation

class EStringTemplateExpression(
    override val location: SourceLocation,
    val entries: List<EStringTemplateEntry>
) : EExpression() {

    init {
        entries.forEach { it.parent = this }
    }

    override var type = C.Kt.STRING.toNoArgumentsType()

    override fun accept(visitor: Visitor) {
        visitor.visitStringTemplateExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        entries.forEach { it.accept(visitor) }
    }

    override fun copy(): EStringTemplateExpression? {
        val copyEntries = entries.map { it.copy() ?: return null }
        return EStringTemplateExpression(location, copyEntries)
    }
}