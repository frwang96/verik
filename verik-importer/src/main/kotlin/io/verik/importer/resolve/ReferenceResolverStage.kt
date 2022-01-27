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

import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object ReferenceResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val namespaceIndexerVisitor = NamespaceIndexerVisitor()
        projectContext.project.accept(namespaceIndexerVisitor)
        val referenceResolverVisitor = ReferenceResolverVisitor(namespaceIndexerVisitor.namespace)
        projectContext.project.accept(referenceResolverVisitor)
    }

    private class NamespaceIndexerVisitor : TreeVisitor() {

        val namespace = Namespace()

        override fun visitSvClass(`class`: ESvClass) {
            namespace[`class`.name] = `class`
        }

        override fun visitTypeAlias(typeAlias: ETypeAlias) {
            namespace[typeAlias.name] = typeAlias
        }

        override fun visitStruct(struct: EStruct) {
            namespace[struct.name] = struct
        }

        override fun visitEnum(enum: EEnum) {
            namespace[enum.name] = enum
        }
    }

    private class ReferenceResolverVisitor(
        private val namespace: Namespace
    ) : TreeVisitor() {

        override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            val declaration = namespace[referenceDescriptor.name]
            if (declaration != null) {
                referenceDescriptor.type = declaration.toType()
            } else {
                Messages.UNRESOLVED_REFERENCE.on(referenceDescriptor, referenceDescriptor.name)
            }
        }
    }
}
