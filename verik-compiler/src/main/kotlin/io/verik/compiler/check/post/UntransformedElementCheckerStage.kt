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
import io.verik.compiler.ast.element.kt.EForExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtPropertyStatement
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreKtAbstractFunctionDeclaration
import io.verik.compiler.core.common.CoreKtPropertyDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object UntransformedElementCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedElementVisitor)
    }

    object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitNullElement(nullElement: EElement) {
            super.visitNullElement(nullElement)
            Messages.INTERNAL_ERROR.on(nullElement, "Unexpected null element")
        }

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            Messages.INTERNAL_ERROR.on(basicClass, "Class ${basicClass.name} $message")
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

        override fun visitKtPropertyStatement(propertyStatement: EKtPropertyStatement) {
            super.visitKtPropertyStatement(propertyStatement)
            Messages.INTERNAL_ERROR.on(propertyStatement, "Property statement $message")
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            Messages.INTERNAL_ERROR.on(unaryExpression, "Unary expression $message")
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression $message")
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            Messages.INTERNAL_ERROR.on(referenceExpression, "Reference expression $message")
        }

        override fun visitSvReferenceExpression(referenceExpression: ESvReferenceExpression) {
            super.visitSvReferenceExpression(referenceExpression)
            if (referenceExpression.reference is CoreKtPropertyDeclaration)
                Messages.INTERNAL_ERROR.on(referenceExpression, "Reference expression reference $message")
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            Messages.INTERNAL_ERROR.on(callExpression, "Call expression $message")
        }

        override fun visitSvCallExpression(callExpression: ESvCallExpression) {
            super.visitSvCallExpression(callExpression)
            if (callExpression.reference is CoreKtAbstractFunctionDeclaration)
                Messages.INTERNAL_ERROR.on(callExpression, "Call expression reference $message")
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            Messages.INTERNAL_ERROR.on(stringTemplateExpression, "String template expression $message")
        }

        override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
            super.visitKtArrayAccessExpression(arrayAccessExpression)
            Messages.INTERNAL_ERROR.on(arrayAccessExpression, "Array access expression $message")
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            Messages.INTERNAL_ERROR.on(whenExpression, "When expression $message")
        }

        override fun visitForExpression(forExpression: EForExpression) {
            super.visitForExpression(forExpression)
            Messages.INTERNAL_ERROR.on(forExpression, "For expression $message")
        }
    }
}
