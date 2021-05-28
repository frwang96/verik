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

package io.verik.compiler.ast

import io.verik.compiler.main.MessageLocation
import java.nio.file.Path

open class VkFile(
    override val location: MessageLocation,
    val inputPath: Path,
    val relativePath: Path,
    val sourceSetType: SourceSetType,
    val packageName: Name,
    val importDirectives: List<VkImportDirective>,
    val declarations: ArrayList<VkDeclaration>
): VkElement() {

    init {
        importDirectives.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
    }

    override fun <R> accept(visitor: VkVisitor<R>): R? {
        return visitor.visitFile(this)
    }

    override fun acceptChildren(visitor: VkTreeVisitor) {
        importDirectives.forEach { it.accept(visitor) }
        declarations.forEach { it.accept(visitor) }
    }
}