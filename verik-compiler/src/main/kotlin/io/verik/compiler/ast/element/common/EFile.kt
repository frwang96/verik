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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.interfaces.ResizableElementContainer
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import java.nio.file.Path
import java.nio.file.Paths

class EFile(
    override val location: SourceLocation,
    val inputPath: Path,
    private val outputPath: Path?,
    var members: ArrayList<EElement>
) : EElement(), ResizableElementContainer {

    init {
        members.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitFile(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        members.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldElement: EElement, newElement: EElement): Boolean {
        newElement.parent = this
        return members.replaceIfContains(oldElement, newElement)
    }

    override fun insertChild(element: EElement) {
        element.parent = this
        members.add(element)
    }

    fun getOutputPathNotNull(): Path {
        return if (outputPath != null) {
            outputPath
        } else {
            Messages.INTERNAL_ERROR.on(location, "File output path not specified")
            Paths.get("")
        }
    }
}
