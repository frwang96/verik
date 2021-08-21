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

package io.verik.compiler.check.normalize

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ElementAliasChecker : NormalizationStage() {

    override fun process(projectContext: ProjectContext) {
        val elementAliasVisitor = ElementAliasVisitor()
        projectContext.project.accept(elementAliasVisitor)
    }

    class ElementAliasVisitor : TreeVisitor() {

        private val elementSet = HashSet<EElement>()

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element in elementSet)
                m.error("Unexpected element aliasing: $element", element)
            elementSet.add(element)
        }
    }
}