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
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ECoverGroup
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CoverGroupInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val coverGroupInterpreterVisitor = CoverGroupInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverGroupInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class CoverGroupInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            if (cls.type.isSubtype(Core.Vk.C_CoverGroup)) {
                val constructor = interpretConstructor(cls.primaryConstructor!!)
                val coverGroup = ECoverGroup(
                    location = cls.location,
                    bodyStartLocation = cls.bodyStartLocation,
                    bodyEndLocation = cls.bodyEndLocation,
                    name = cls.name,
                    type = cls.type,
                    annotationEntries = cls.annotationEntries,
                    documentationLines = cls.documentationLines,
                    typeParameters = cls.typeParameters,
                    declarations = cls.declarations,
                    constructor = constructor
                )
                referenceUpdater.replace(cls, coverGroup)
            }
        }

        private fun interpretConstructor(primaryConstructor: EPrimaryConstructor): ESvConstructor {
            val valueParameters = ArrayList<ESvValueParameter>()
            primaryConstructor.valueParameters.forEach {
                val valueParameter = ESvValueParameter(
                    location = it.location,
                    name = it.name,
                    type = it.type,
                    annotationEntries = it.annotationEntries,
                    expression = it.expression,
                    isInput = true
                )
                referenceUpdater.update(it, valueParameter)
                valueParameters.add(valueParameter)
            }
            val constructor = ESvConstructor(
                location = primaryConstructor.location,
                type = primaryConstructor.type,
                annotationEntries = primaryConstructor.annotationEntries,
                documentationLines = primaryConstructor.documentationLines,
                body = primaryConstructor.body,
                valueParameters = valueParameters
            )
            referenceUpdater.update(primaryConstructor, constructor)
            return constructor
        }
    }
}
