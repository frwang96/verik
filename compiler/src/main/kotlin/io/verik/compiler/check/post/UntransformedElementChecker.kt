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

import io.verik.compiler.ast.element.common.CCallExpression
import io.verik.compiler.ast.element.kt.*
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object UntransformedElementChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.verikFiles.forEach {
            it.accept(UntransformedElementVisitor)
        }
    }

    object UntransformedElementVisitor : TreeVisitor() {

        private const val message = "has not been transformed to SystemVerilog"

        override fun visitKBasicClass(basicClass: KBasicClass) {
            super.visitKBasicClass(basicClass)
            m.error("Class $basicClass $message", basicClass)
        }

        override fun visitKFunction(function: KFunction) {
            super.visitKFunction(function)
            m.error("Function $function $message", function)
        }

        override fun visitKProperty(property: KProperty) {
            super.visitKProperty(property)
            m.error("Property $property $message", property)
        }

        override fun visitKBlockExpression(blockExpression: KBlockExpression) {
            super.visitKBlockExpression(blockExpression)
            m.error("Block expression $message", blockExpression)
        }

        override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
            super.visitKBinaryExpression(binaryExpression)
            m.error("Binary expression $message", binaryExpression)
        }

        override fun visitCCallExpression(callExpression: CCallExpression) {
            super.visitCCallExpression(callExpression)
            if (callExpression.reference is CoreKtFunctionDeclaration)
                m.error("Call expression $message: ${callExpression.reference}", callExpression)
        }

        override fun visitKStringTemplateExpression(stringTemplateExpression: KStringTemplateExpression) {
            super.visitKStringTemplateExpression(stringTemplateExpression)
            m.error("String template expression $message", stringTemplateExpression)
        }
    }
}