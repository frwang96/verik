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
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object UntransformedElementChecker : PostCheckerStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedElementVisitor)
    }

    object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitNullElement(nullElement: EElement) {
            super.visitNullElement(nullElement)
            m.error("Unexpected null element", nullElement)
        }

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            m.error("Class ${basicClass.name} $message", basicClass)
        }

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            m.error("Function ${function.name} $message", function)
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            m.error("Property ${property.name} $message", property)
        }

        override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
            super.visitKtEnumEntry(enumEntry)
            m.error("Enum entry ${enumEntry.name} $message", enumEntry)
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            m.error("Block expression $message", blockExpression)
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            m.error("Unary expression $message", unaryExpression)
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            m.error("Binary expression $message", binaryExpression)
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            m.error("Reference expression $message", referenceExpression)
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            m.error("Call expression $message", callExpression)
        }

        override fun visitSvCallExpression(callExpression: ESvCallExpression) {
            super.visitSvCallExpression(callExpression)
            if (callExpression.reference is CoreKtFunctionDeclaration)
                m.error("Call expression $message: ${callExpression.reference.name}", callExpression)
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            m.error("String template expression $message", stringTemplateExpression)
        }
    }
}
