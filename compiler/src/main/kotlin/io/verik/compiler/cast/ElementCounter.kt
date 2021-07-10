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
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.ast.element.VkKtClass
import io.verik.compiler.ast.element.VkKtFunction
import io.verik.compiler.ast.element.VkKtProperty
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ElementCounter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val elementVisitor = ElementVisitor()
        projectContext.vkFiles.forEach { it.accept(elementVisitor) }
        m.log("Count: Files: ${elementVisitor.fileCount}")
        m.log("Count: Classes: ${elementVisitor.classCount}")
        m.log("Count: Functions: ${elementVisitor.functionCount}")
        m.log("Count: Properties: ${elementVisitor.propertyCount}")
    }

    class ElementVisitor : TreeVisitor() {

        var fileCount = 0
        var classCount = 0
        var functionCount = 0
        var propertyCount = 0

        override fun visitFile(file: VkFile) {
            super.visitFile(file)
            fileCount++
        }

        override fun visitKtClass(ktClass: VkKtClass) {
            super.visitKtClass(ktClass)
            classCount++
        }

        override fun visitKtFunction(ktFunction: VkKtFunction) {
            super.visitKtFunction(ktFunction)
            functionCount++
        }

        override fun visitKtProperty(ktProperty: VkKtProperty) {
            super.visitKtProperty(ktProperty)
            propertyCount++
        }
    }
}