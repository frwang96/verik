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

package io.verik.compiler.ast.element

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.main.MessageLocation

class VkModule(
    name: Name,
    type: Type,
    location: MessageLocation,
    declarations: ArrayList<VkDeclaration>
): VkBaseClass(name, type, location, declarations) {

    override fun <R> accept(visitor: Visitor<R>): R? {
        return visitor.visitModule(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}

    override fun copy(): VkModule {
        return VkModule(
            name,
            type.copy(),
            location,
            ArrayList(declarations.map { it.copy() }),
        )
    }
}