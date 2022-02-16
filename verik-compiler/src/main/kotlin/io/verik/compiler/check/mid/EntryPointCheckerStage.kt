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

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

object EntryPointCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val entryPointCheckerVisitor = EntryPointCheckerVisitor()
        projectContext.project.accept(entryPointCheckerVisitor)
        projectContext.config.entryPoints.forEach {
            if (it !in entryPointCheckerVisitor.names) {
                Messages.ENTRY_POINT_NOT_FOUND.on(SourceLocation.NULL, it)
            }
        }
    }

    private class EntryPointCheckerVisitor : TreeVisitor() {

        val names = ArrayList<String>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            if (declaration.hasAnnotationEntry(AnnotationEntries.ENTRY)) {
                names.add(declaration.name)
            }
        }
    }
}
