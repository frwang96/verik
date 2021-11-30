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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.TargetDeclaration

object NameCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(NameCheckerVisitor)
    }

    private object NameCheckerVisitor : TreeVisitor() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9$]*")

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDeclaration && element !is ERootPackage) {
                if (!element.name.matches(nameRegex))
                    Messages.NAME_ILLEGAL.on(element, element.name)
            }
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference !is CoreDeclaration && !reference.name.matches(nameRegex))
                Messages.NAME_ILLEGAL.on(referenceExpression, reference.name)
        }

        override fun visitSvCallExpression(callExpression: ESvCallExpression) {
            super.visitSvCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference !is CoreDeclaration && reference !is TargetDeclaration) {
                if (!reference.name.matches(nameRegex))
                    Messages.NAME_ILLEGAL.on(callExpression, reference.name)
            }
        }
    }
}
