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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.main.ProjectContext

object NameRelabelerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(NameRelabelerVisitor)
    }

    object NameRelabelerVisitor : TreeVisitor() {

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is Declaration && element is Annotated) {
                val annotation = element.getAnnotation(Annotations.RELABEL)
                if (annotation != null)
                    element.name = annotation.arguments[0]
            }
        }
    }
}
