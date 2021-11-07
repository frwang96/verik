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
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object TemporaryDeclarationRelabelerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val temporaryDeclarationRelabelerVisitor = TemporaryDeclarationRelabelerVisitor()
        projectContext.project.accept(temporaryDeclarationRelabelerVisitor)
    }

    private class TemporaryDeclarationRelabelerVisitor : TreeVisitor() {

        var index = 0

        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            index = 0
            super.visitAbstractFunction(abstractFunction)
        }

        override fun visitSvProperty(property: ESvProperty) {
            super.visitSvProperty(property)
            if (property.name == "<tmp>") {
                property.name = "_$$index"
                index++
            }
        }

        override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
            super.visitSvValueParameter(valueParameter)
            if (valueParameter.name == "<tmp>") {
                valueParameter.name = "_$$index"
                index++
            }
        }
    }
}
