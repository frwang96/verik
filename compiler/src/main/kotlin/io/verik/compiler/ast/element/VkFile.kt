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

import io.verik.compiler.ast.common.PackageName
import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.main.MessageLocation
import io.verik.compiler.main.m
import java.nio.file.Path

open class VkFile(
    override val location: MessageLocation,
    val inputPath: Path,
    val relativePath: Path,
    val sourceSetType: SourceSetType,
    val packageName: PackageName,
    val declarations: ArrayList<VkDeclaration>,
    private val importDirectives: List<VkImportDirective>
): VkElement() {

    init {
        importDirectives.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitFile(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        importDirectives.forEach { it.accept(visitor) }
        declarations.forEach { it.accept(visitor) }
    }

    fun replaceChild(oldDeclaration: VkDeclaration, newDeclaration: VkDeclaration) {
        val index = declarations.indexOf(oldDeclaration)
        if (index == -1) {
            m.error("Could not find declaration $oldDeclaration", oldDeclaration)
        } else {
            newDeclaration.parent = this
            declarations[index] = newDeclaration
        }
    }

    fun insertChild(oldDeclaration: VkDeclaration, newDeclaration: VkDeclaration) {
        val index = declarations.indexOf(oldDeclaration)
        if (index == -1) {
            m.error("Could not find declaration $oldDeclaration", oldDeclaration)
        } else {
            newDeclaration.parent = this
            declarations.add(index + 1, newDeclaration)
        }
    }
}