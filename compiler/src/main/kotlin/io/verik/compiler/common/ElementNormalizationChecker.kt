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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

object ElementNormalizationChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val elementNormalizationVisitor = ElementNormalizationVisitor()
        projectContext.files.forEach {
            it.accept(elementNormalizationVisitor)
        }
    }

    class ElementNormalizationVisitor : TreeVisitor() {

        private val parentStack = ArrayDeque<EElement>()

        override fun visitElement(element: EElement) {
            val parent = element.parentNotNull()
            val expectedParent = parentStack.last()
            if (parent != expectedParent) {
                val expectedString = "Expected ${expectedParent::class.simpleName} but was ${parent::class.simpleName}"
                m.error("Mismatch in parent element of ${element::class.simpleName}: $expectedString", element)
            }
            parentStack.push(element)
            super.visitElement(element)
            parentStack.pop()
        }

        override fun visitFile(file: EFile) {
            if (file.parent != null)
                m.error("Parent element should be null", file)
            parentStack.push(file)
            file.acceptChildren(this)
            parentStack.pop()
            file.members.forEach {
                if (it !is Declaration)
                    m.fatal("Declaration expected but got: ${it::class.simpleName}", it)
            }
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            super.visitAbstractClass(abstractClass)
            abstractClass.members.forEach {
                if (it !is Declaration)
                    m.fatal("Declaration expected but got: ${it::class.simpleName}", it)
            }
        }
    }
}