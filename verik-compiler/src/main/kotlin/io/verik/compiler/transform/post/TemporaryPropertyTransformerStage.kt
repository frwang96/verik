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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.common.ETemporaryValueParameter
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object TemporaryPropertyTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val temporaryPropertyRelabelerVisitor = TemporaryPropertyRelabelerVisitor(referenceUpdater)
        projectContext.project.accept(temporaryPropertyRelabelerVisitor)
        referenceUpdater.flush()
    }

    private class TemporaryPropertyRelabelerVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        var index = 0

        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            index = 0
            super.visitAbstractFunction(abstractFunction)
        }

        override fun visitTemporaryProperty(temporaryProperty: ETemporaryProperty) {
            super.visitTemporaryProperty(temporaryProperty)
            val name = "_$$index"
            index++
            val property = ESvProperty(
                temporaryProperty.location,
                name,
                temporaryProperty.type,
                temporaryProperty.initializer,
                false
            )
            referenceUpdater.replace(temporaryProperty, property)
        }

        override fun visitTemporaryValueParameter(temporaryValueParameter: ETemporaryValueParameter) {
            super.visitTemporaryValueParameter(temporaryValueParameter)
            val name = "_$$index"
            index++
            val valueParameter = ESvValueParameter(
                temporaryValueParameter.location,
                name,
                temporaryValueParameter.type
            )
            referenceUpdater.replace(temporaryValueParameter, valueParameter)
        }
    }
}
