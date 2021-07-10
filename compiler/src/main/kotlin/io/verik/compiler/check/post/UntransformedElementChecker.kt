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

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.*
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object UntransformedElementChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(UntransformedElementVisitor)
        }
    }

    object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitKtClass(ktClass: VkKtClass) {
            super.visitKtClass(ktClass)
            m.error("Class $ktClass $message", ktClass)
        }

        override fun visitKtFunction(ktFunction: VkKtFunction) {
            super.visitKtFunction(ktFunction)
            m.error("Function $ktFunction $message", ktFunction)
        }

        override fun visitKtProperty(ktProperty: VkKtProperty) {
            super.visitKtProperty(ktProperty)
            m.error("Property $ktProperty $message", ktProperty)
        }

        override fun visitKtBinaryExpression(ktBinaryExpression: VkKtBinaryExpression) {
            super.visitKtBinaryExpression(ktBinaryExpression)
            m.error("Binary expression $message", ktBinaryExpression)
        }

        override fun visitCallExpression(callExpression: VkCallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference is CoreKtFunctionDeclaration)
                m.error("Call expression ${callExpression.reference} $message", callExpression)
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: VkStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            m.error("String template expression $message", stringTemplateExpression)
        }
    }
}