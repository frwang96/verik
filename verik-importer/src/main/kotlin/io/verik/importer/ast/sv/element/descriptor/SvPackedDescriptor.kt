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

package io.verik.importer.ast.sv.element.descriptor

import io.verik.importer.ast.sv.element.expression.SvExpression
import io.verik.importer.common.SvVisitor
import io.verik.importer.common.Type
import io.verik.importer.message.SourceLocation

class SvPackedDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    val descriptor: SvDescriptor,
    val left: SvExpression,
    val right: SvExpression
) : SvDescriptor() {

    override fun accept(visitor: SvVisitor) {
        visitor.visitPackedDescriptor(this)
    }

    override fun acceptChildren(visitor: SvVisitor) {
        descriptor.accept(visitor)
        left.accept(visitor)
        right.accept(visitor)
    }
}
