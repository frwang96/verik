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

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object StructInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val structInterpreterVisitor = StructInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(structInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class StructInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            if (basicClass.toType().isSubtype(Core.Vk.C_Struct.toType())) {
                val properties = basicClass.primaryConstructor!!
                    .valueParameters
                    .map { interpretProperty(it, referenceUpdater) }
                val struct = EStruct(basicClass.location, basicClass.name, properties)
                referenceUpdater.replace(basicClass, struct)
                referenceUpdater.update(basicClass.primaryConstructor!!, struct)
            }
        }

        private fun interpretProperty(
            valueParameter: EKtValueParameter,
            referenceUpdater: ReferenceUpdater
        ): ESvProperty {
            val property = ESvProperty(
                valueParameter.location,
                valueParameter.name,
                valueParameter.type,
                null,
                valueParameter.isMutable,
                null
            )
            referenceUpdater.update(valueParameter, property)
            return property
        }
    }
}
