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

package io.verik.importer.ast.sv.element.declaration

import io.verik.importer.common.SvVisitor
import io.verik.importer.core.Core
import io.verik.importer.message.SourceLocation

class SvPackage(
    override val location: SourceLocation,
    override val name: String,
    override var declarations: ArrayList<SvDeclaration>
) : SvContainerDeclaration() {

    override val signature: String? = null
    override var type = Core.C_Unit.toType()

    init {
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: SvVisitor) {
        visitor.visitPackage(this)
    }

    override fun acceptChildren(visitor: SvVisitor) {
        declarations.forEach { it.accept(visitor) }
    }
}