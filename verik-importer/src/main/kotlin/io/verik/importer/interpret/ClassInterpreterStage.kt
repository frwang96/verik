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

package io.verik.importer.interpret

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.common.ReferenceUpdater
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

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

        override fun visitSvClass(`class`: ESvClass) {
            super.visitSvClass(`class`)
            val interpretedClass = EKtClass(
                `class`.location,
                `class`.name,
                `class`.signature,
                `class`.declarations,
                listOf(),
                `class`.superDescriptor,
                true
            )
            referenceUpdater.replace(`class`, interpretedClass)
        }

        override fun visitModule(module: EModule) {
            super.visitModule(module)
            val valueParameters = module.ports.map {
                val annotationEntry = it.portType.getAnnotationEntry()
                val valueParameter = EKtValueParameter(
                    it.location,
                    it.name,
                    it.descriptor,
                    listOf(annotationEntry),
                    true
                )
                referenceUpdater.update(it, valueParameter)
                valueParameter
            }
            val superDescriptor = ESimpleDescriptor(module.location, Core.C_Module.toType())
            val interpretedClass = EKtClass(
                module.location,
                module.name,
                module.signature,
                module.declarations,
                valueParameters,
                superDescriptor,
                false
            )
            referenceUpdater.replace(module, interpretedClass)
        }
    }
}
