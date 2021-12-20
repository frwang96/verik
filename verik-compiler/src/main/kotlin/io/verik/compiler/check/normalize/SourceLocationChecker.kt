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
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import java.nio.file.Path

object SourceLocationChecker : NormalizationStage {

    override fun process(projectContext: ProjectContext, projectStage: ProjectStage) {
        projectContext.project.files().forEach {
            val sourceLocationVisitor = SourceLocationVisitor(projectContext, projectStage, it.inputPath)
            it.accept(sourceLocationVisitor)
        }
    }

    private class SourceLocationVisitor(
        private val projectContext: ProjectContext,
        private val projectStage: ProjectStage,
        private val path: Path
    ) : TreeVisitor() {

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element.location.path != path) {
                val expectedPath = projectContext.config.projectDir.relativize(path)
                val actualPath = projectContext.config.projectDir.relativize(element.location.path)
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Mismatch in path for source location: Expected $expectedPath actual $actualPath"
                )
            }
        }
    }
}
