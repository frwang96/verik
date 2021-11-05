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
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object ComponentInstantiationCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ComponentInstantiationCheckerVisitor)
    }

    private object ComponentInstantiationCheckerVisitor : TreeVisitor() {

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            if (property.type.isSubtype(Core.Vk.C_Component.toType())) {
                val parent = property.parent
                if (parent is EKtBasicClass && parent.toType().isSubtype(Core.Vk.C_Component.toType())) {
                    if (!property.hasAnnotation(Annotations.MAKE))
                        Messages.MAKE_ANNOTATION_REQUIRED.on(property)
                } else {
                    if (property.hasAnnotation(Annotations.MAKE))
                        Messages.COMPONENT_INSTANTIATION_OUT_OF_CONTEXT.on(property)
                }
            } else {
                if (property.hasAnnotation(Annotations.MAKE))
                    Messages.MAKE_ANNOTATION_ILLEGAL.on(property)
            }
        }
    }
}
