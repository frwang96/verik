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
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object NameCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(NameCheckerVisitor)
    }

    object NameCheckerVisitor : TreeVisitor() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9$]*")

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is Declaration) {
                if (!element.name.matches(nameRegex))
                    Messages.NAME_ILLEGAL.on(element, element.name)
            }
        }

        override fun visitSvReferenceExpression(referenceExpression: ESvReferenceExpression) {
            super.visitSvReferenceExpression(referenceExpression)
            if (!referenceExpression.reference.name.matches(nameRegex))
                Messages.NAME_ILLEGAL.on(referenceExpression, referenceExpression.reference.name)
        }

        override fun visitSvCallExpression(callExpression: ESvCallExpression) {
            super.visitSvCallExpression(callExpression)
            if (!callExpression.reference.name.matches(nameRegex))
                Messages.NAME_ILLEGAL.on(callExpression, callExpression.reference.name)
        }
    }
}
