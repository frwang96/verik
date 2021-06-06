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

package io.verik.compiler.cast

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.*
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ElementCounter {

    fun count(projectContext: ProjectContext) {
        val elementVisitor = ElementVisitor()
        projectContext.vkFiles.forEach { it.accept(elementVisitor) }
        m.log("Count: Elements: ${elementVisitor.elementCount}")
        m.log("Count: Files: ${elementVisitor.fileCount}")
        m.log("Count: Declarations: ${elementVisitor.declarationCount}")
        m.log("Count: Classes: ${elementVisitor.classCount}")
        m.log("Count: Functions: ${elementVisitor.functionCount}")
        m.log("Count: Properties: ${elementVisitor.propertyCount}")
    }

    class ElementVisitor: TreeVisitor() {

        var elementCount = 0
        var fileCount = 0
        var declarationCount = 0
        var classCount = 0
        var functionCount = 0
        var propertyCount = 0

        override fun visitElement(element: VkElement) {
            super.visitElement(element)
            elementCount++
        }

        override fun visitFile(file: VkFile) {
            super.visitFile(file)
            fileCount++
        }

        override fun visitDeclaration(declaration: VkDeclaration) {
            super.visitDeclaration(declaration)
            declarationCount++
        }

        override fun visitBaseClass(baseClass: VkBaseClass) {
            super.visitBaseClass(baseClass)
            classCount++
        }

        override fun visitBaseFunction(baseFunction: VkBaseFunction) {
            super.visitBaseFunction(baseFunction)
            functionCount++
        }

        override fun visitBaseProperty(baseProperty: VkBaseProperty) {
            super.visitBaseProperty(baseProperty)
            propertyCount++
        }
    }
}