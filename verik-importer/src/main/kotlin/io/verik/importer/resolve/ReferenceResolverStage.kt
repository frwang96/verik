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

import io.verik.importer.ast.sv.element.descriptor.SvReferenceDescriptor
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.core.CoreClassDeclaration
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object ReferenceResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.compilationUnit.accept(ReferenceResolverVisitor)
    }

    private object ReferenceResolverVisitor : SvTreeVisitor() {

        override fun visitReferenceDescriptor(referenceDescriptor: SvReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            referenceDescriptor.type = CoreClassDeclaration(referenceDescriptor.name).toType()
        }
    }
}
