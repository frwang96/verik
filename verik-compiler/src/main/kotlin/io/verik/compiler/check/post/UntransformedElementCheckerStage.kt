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

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.kt.EAsExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object UntransformedElementCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedElementVisitor)
    }

    private object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitKtClass(cls: EKtClass) {
            Messages.INTERNAL_ERROR.on(cls, "Class ${cls.name} $message")
        }

        override fun visitKtFunction(function: EKtFunction) {
            Messages.INTERNAL_ERROR.on(function, "Function ${function.name} $message")
        }

        override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
            Messages.INTERNAL_ERROR.on(primaryConstructor, "Primary constructor ${primaryConstructor.name} $message")
        }

        override fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
            Messages.INTERNAL_ERROR.on(
                secondaryConstructor,
                "Secondary constructor ${secondaryConstructor.name} $message"
            )
        }

        override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
            Messages.INTERNAL_ERROR.on(valueParameter, "Value parameter ${valueParameter.name} $message")
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            Messages.INTERNAL_ERROR.on(unaryExpression, "Unary expression $message")
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression $message")
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            Messages.INTERNAL_ERROR.on(stringTemplateExpression, "String template expression $message")
        }

        override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
            Messages.INTERNAL_ERROR.on(arrayAccessExpression, "Array access expression $message")
        }

        override fun visitIsExpression(isExpression: EIsExpression) {
            Messages.INTERNAL_ERROR.on(isExpression, "Is expression $message")
        }

        override fun visitAsExpression(asExpression: EAsExpression) {
            Messages.INTERNAL_ERROR.on(asExpression, "As expression $message")
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            Messages.INTERNAL_ERROR.on(whenExpression, "When expression $message")
        }

        override fun visitKtForStatement(forStatement: EKtForStatement) {
            Messages.INTERNAL_ERROR.on(forStatement, "For statement $message")
        }
    }
}
