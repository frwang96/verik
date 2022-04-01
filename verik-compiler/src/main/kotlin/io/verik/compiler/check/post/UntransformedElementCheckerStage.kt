/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EInitializerBlock
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
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

/**
 * Stage that checks for elements that have not been transformed to SystemVerilog.
 */
object UntransformedElementCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedElementVisitor)
    }

    private object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitKtClass(cls: EKtClass) {
            Messages.INTERNAL_ERROR.on(cls, "Class ${cls.name} $message")
        }

        override fun visitCompanionObject(companionObject: ECompanionObject) {
            Messages.INTERNAL_ERROR.on(companionObject, "Companion object $message")
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

        override fun visitInitializerBlock(initializerBlock: EInitializerBlock) {
            Messages.INTERNAL_ERROR.on(initializerBlock, "Initializer block $message")
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

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            if (referenceExpression.isSafeAccess) {
                Messages.INTERNAL_ERROR.on(referenceExpression, "Reference expression $message")
            }
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            if (callExpression.isSafeAccess) {
                Messages.INTERNAL_ERROR.on(callExpression, "Call expression $message")
            }
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
