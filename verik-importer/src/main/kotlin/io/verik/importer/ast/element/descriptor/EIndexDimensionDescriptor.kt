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
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EIndexDimensionDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    override var descriptor: EDescriptor,
    var index: EDescriptor
) : EContainerDescriptor() {

    init {
        descriptor.parent = this
        index.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitIndexDimensionDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        index.accept(visitor)
    }

    override fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean {
        if (super.replaceChild(oldDescriptor, newDescriptor))
            return true
        return if (oldDescriptor == index) {
            index = newDescriptor
            true
        } else false
    }
}
