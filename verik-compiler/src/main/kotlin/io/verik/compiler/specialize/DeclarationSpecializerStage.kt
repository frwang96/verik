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

package io.verik.compiler.specialize

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.copy.CopyContext
import io.verik.compiler.copy.DeclarationCopierVisitor
import io.verik.compiler.copy.ElementCopier
import io.verik.compiler.copy.ReferenceForwardingMap
import io.verik.compiler.main.ProjectContext

object DeclarationSpecializerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceForwardingMap = ReferenceForwardingMap()
        val declarationCopierVisitor = DeclarationCopierVisitor(referenceForwardingMap)
        projectContext.project.files().forEach { file ->
            file.declarations.forEach {
                it.accept(declarationCopierVisitor)
            }
        }
        val copyContext = CopyContext(referenceForwardingMap)
        projectContext.project.files().forEach { file ->
            val declarations = file.declarations.map { ElementCopier.copy(it, copyContext) }
            file.declarations = ArrayList(declarations)
        }
    }
}
