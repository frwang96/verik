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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object StructInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val structInterpreterVisitor = StructInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(structInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class StructInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            if (cls.type.isSubtype(Core.Vk.C_Struct)) {
                val properties = cls.primaryConstructor!!
                    .valueParameters
                    .map { interpretProperty(it, referenceUpdater) }
                val struct = EStruct(
                    cls.location,
                    cls.bodyStartLocation,
                    cls.bodyEndLocation,
                    cls.name,
                    cls.type,
                    cls.annotationEntries,
                    cls.documentationLines,
                    properties
                )
                referenceUpdater.replace(cls, struct)
                referenceUpdater.update(cls.primaryConstructor!!, struct)
            }
        }

        private fun interpretProperty(
            valueParameter: EKtValueParameter,
            referenceUpdater: ReferenceUpdater
        ): EProperty {
            val property = EProperty.named(
                location = valueParameter.location,
                name = valueParameter.name,
                type = valueParameter.type,
                initializer = null,
                isMutable = valueParameter.isMutable
            )
            referenceUpdater.update(valueParameter, property)
            return property
        }
    }
}
