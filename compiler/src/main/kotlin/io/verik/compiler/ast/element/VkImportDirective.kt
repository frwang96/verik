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
import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.main.MessageLocation

class VkImportDirective(
    override val location: MessageLocation,
    val name: Name?,
    val packageName: QualifiedName
): VkElement() {

    override fun <R> accept(visitor: Visitor<R>): R? {
        return visitor.visitImportDirective(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}

    override fun copy(): VkImportDirective {
        return VkImportDirective(location, name, packageName)
    }
}