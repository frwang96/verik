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
import io.verik.compiler.util.ElementUtil

open class VkBaseClass(
    override var name: Name,
    override val location: MessageLocation,
    val type: Type,
    val baseFunctions: ArrayList<VkBaseFunction>,
    val baseProperties: ArrayList<VkBaseProperty>
): VkDeclaration() {

    init {
        baseFunctions.forEach { it.parent = this }
        baseProperties.forEach { it.parent = this }
    }

    override fun <R> accept(visitor: Visitor<R>): R? {
        return visitor.visitBaseClass(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        baseFunctions.forEach { it.accept(visitor) }
        baseProperties.forEach { it.accept(visitor) }
    }

    fun replace(baseClass: VkBaseClass) {
        ElementUtil.cast<VkFile>(parent)?.replaceDeclaration(this, baseClass)
    }
}