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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ClassInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val classInterpreterVisitor = ClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val interpretedClass = ESvClass(
                location = cls.location,
                bodyStartLocation = cls.bodyStartLocation,
                bodyEndLocation = cls.bodyEndLocation,
                name = cls.name,
                type = cls.type,
                annotationEntries = cls.annotationEntries,
                documentationLines = cls.documentationLines,
                superType = cls.superType,
                typeParameters = cls.typeParameters,
                declarations = cls.declarations,
                isObject = cls.isObject
            )
            referenceUpdater.replace(cls, interpretedClass)
        }
    }
}
