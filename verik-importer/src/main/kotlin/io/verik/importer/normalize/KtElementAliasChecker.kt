/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.normalize

import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.common.KtTreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object KtElementAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val elementAliasVisitor = ElementAliasVisitor(projectStage)
        projectContext.project.accept(elementAliasVisitor)
    }

    private class ElementAliasVisitor(
        private val projectStage: ProjectStage
    ) : KtTreeVisitor() {

        private val elementSet = HashSet<KtElement>()

        override fun visitElement(element: KtElement) {
            super.visitElement(element)
            if (element in elementSet)
                Messages.NORMALIZATION_ERROR.on(element, projectStage, "Unexpected element aliasing: $element")
            elementSet.add(element)
        }
    }
}
