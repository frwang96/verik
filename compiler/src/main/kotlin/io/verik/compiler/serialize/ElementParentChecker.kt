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

package io.verik.compiler.serialize

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object ElementParentChecker {

    fun check(projectContext: ProjectContext) {
        val elementParentVisitor = ElementParentVisitor()
        projectContext.vkFiles.forEach {
            it.accept(elementParentVisitor)
        }
    }

    class ElementParentVisitor : TreeVisitor() {

        private val parentStack = ArrayDeque<VkElement>()

        override fun visitElement(element: VkElement) {
            if (element.parent == null)
                m.error("Parent element of ${element::class.simpleName} should not be null", element)
            if (element.parent != parentStack.peek())
                m.error("Mismatch in parent element of ${element::class.simpleName}", element)
            parentStack.push(element)
            super.visitElement(element)
            parentStack.pop()
        }

        override fun visitFile(file: VkFile) {
            if (file.parent != null)
                m.error("Parent element should be null", file)
            parentStack.push(file)
            file.acceptChildren(this)
            parentStack.pop()
        }
    }
}