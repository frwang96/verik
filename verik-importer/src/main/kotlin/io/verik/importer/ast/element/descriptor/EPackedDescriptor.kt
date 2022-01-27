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

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.expression.EExpression
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EPackedDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    val descriptor: EDescriptor,
    val left: EExpression,
    val right: EExpression
) : EDescriptor() {

    init {
        descriptor.parent = this
        left.parent = this
        right.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPackedDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        descriptor.accept(visitor)
        left.accept(visitor)
        right.accept(visitor)
    }
}
