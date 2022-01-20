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

import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.property.PortType
import io.verik.importer.common.SvVisitor
import io.verik.importer.common.Type
import io.verik.importer.message.SourceLocation

class SvPort(
    override val location: SourceLocation,
    override val name: String,
    override var type: Type,
    val descriptor: SvDescriptor,
    val portType: PortType
) : SvDeclaration() {

    override val signature: String? = null

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: SvVisitor) {
        visitor.visitPort(this)
    }

    override fun acceptChildren(visitor: SvVisitor) {
        descriptor.accept(visitor)
    }
}