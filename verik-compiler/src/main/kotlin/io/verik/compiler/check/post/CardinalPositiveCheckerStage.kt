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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object CardinalPositiveCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CardinalPositiveCheckerVisitor)
    }

    private object CardinalPositiveCheckerVisitor : TreeVisitor() {

        private fun checkType(type: Type, element: EElement): Boolean {
            if (type.arguments.any { !checkType(it, element) })
                return false
            return if (type.isCardinalType()) {
                return type.asCardinalValue(element) > 0
            } else true
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            if (!checkType(typedElement.type, typedElement))
                Messages.CARDINAL_NOT_POSITIVE.on(typedElement, typedElement.type)
        }
    }
}
