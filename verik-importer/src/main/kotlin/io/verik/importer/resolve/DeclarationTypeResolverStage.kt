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

package io.verik.importer.resolve

import io.verik.importer.ast.sv.element.declaration.SvFunction
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.declaration.SvTypeAlias
import io.verik.importer.ast.sv.element.declaration.SvValueParameter
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object DeclarationTypeResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.compilationUnit.accept(TypeResolverVisitor)
    }

    private object TypeResolverVisitor : SvTreeVisitor() {

        override fun visitTypeAlias(typeAlias: SvTypeAlias) {
            typeAlias.type = typeAlias.descriptor.type
        }

        override fun visitFunction(function: SvFunction) {
            super.visitFunction(function)
            function.type = function.descriptor.type
        }

        override fun visitProperty(property: SvProperty) {
            property.type = property.descriptor.type
        }

        override fun visitValueParameter(valueParameter: SvValueParameter) {
            valueParameter.type = valueParameter.descriptor.type
        }

        override fun visitPort(port: SvPort) {
            port.type = port.descriptor.type
        }
    }
}
