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

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ElementCounter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        if (projectContext.config.debug) {
            val elementVisitor = ElementVisitor()
            projectContext.project.accept(elementVisitor)
            m.log("Count: Files: ${elementVisitor.fileCount}")
            m.log("Count: Classes: ${elementVisitor.classCount}")
            m.log("Count: Functions: ${elementVisitor.functionCount}")
            m.log("Count: Properties: ${elementVisitor.propertyCount}")
        }
    }

    class ElementVisitor : TreeVisitor() {

        var fileCount = 0
        var classCount = 0
        var functionCount = 0
        var propertyCount = 0

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            fileCount++
        }

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            classCount++
        }

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            functionCount++
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            propertyCount++
        }
    }
}