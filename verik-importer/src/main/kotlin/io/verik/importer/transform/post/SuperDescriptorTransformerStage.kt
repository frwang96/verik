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

package io.verik.importer.transform.post

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object SuperDescriptorTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SuperDescriptorTransformerVisitor)
    }

    object SuperDescriptorTransformerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val superDescriptor = cls.superDescriptor
            if (superDescriptor.type.reference is ETypeParameter) {
                val simpleDescriptor = ESimpleDescriptor(
                    superDescriptor.location,
                    Core.C_Any.toType()
                )
                simpleDescriptor.parent = cls
                cls.superDescriptor = simpleDescriptor
            }
        }
    }
}
