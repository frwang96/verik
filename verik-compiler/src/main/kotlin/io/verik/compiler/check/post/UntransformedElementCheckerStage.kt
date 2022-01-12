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

import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtForStatement
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
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

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            Messages.INTERNAL_ERROR.on(`class`, "Class ${`class`.name} $message")
        }

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            Messages.INTERNAL_ERROR.on(function, "Function ${function.name} $message")
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            Messages.INTERNAL_ERROR.on(property, "Property ${property.name} $message")
        }

        override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
            super.visitKtEnumEntry(enumEntry)
            Messages.INTERNAL_ERROR.on(enumEntry, "Enum entry ${enumEntry.name} $message")
        }

        override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
            super.visitKtValueParameter(valueParameter)
            Messages.INTERNAL_ERROR.on(valueParameter, "Value parameter ${valueParameter.name} $message")
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            Messages.INTERNAL_ERROR.on(blockExpression, "Block expression $message")
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            Messages.INTERNAL_ERROR.on(unaryExpression, "Unary expression $message")
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression $message")
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            Messages.INTERNAL_ERROR.on(stringTemplateExpression, "String template expression $message")
        }

        override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
            super.visitKtArrayAccessExpression(arrayAccessExpression)
            Messages.INTERNAL_ERROR.on(arrayAccessExpression, "Array access expression $message")
        }

        override fun visitIsExpression(isExpression: EIsExpression) {
            super.visitIsExpression(isExpression)
            Messages.INTERNAL_ERROR.on(isExpression, "Is expression $message")
        }

        override fun visitAsExpression(asExpression: EAsExpression) {
            super.visitAsExpression(asExpression)
            Messages.INTERNAL_ERROR.on(asExpression, "As expression $message")
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            Messages.INTERNAL_ERROR.on(whenExpression, "When expression $message")
        }

        override fun visitKtForStatement(forStatement: EKtForStatement) {
            super.visitKtForStatement(forStatement)
            Messages.INTERNAL_ERROR.on(forStatement, "For statement $message")
        }
    }
}
