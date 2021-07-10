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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.element.kt.*
import io.verik.compiler.ast.element.sv.*

class ElementPrinter : Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitCFile(file: CFile) {
        build("CFile") {
            build(file.declarations)
        }
    }

    override fun visitKBasicClass(basicClass: KBasicClass) {
        build("KBasicClass") {
            build(basicClass.name.toString())
            build(basicClass.typeParameters)
            build(basicClass.declarations)
        }
    }

    override fun visitSBasicClass(basicClass: SBasicClass) {
        build("SBasicClass") {
            build(basicClass.name.toString())
            build(basicClass.typeParameters)
            build(basicClass.declarations)
        }
    }

    override fun visitSModule(module: SModule) {
        build("SModule") {
            build(module.name.toString())
        }
    }

    override fun visitKFunction(function: KFunction) {
        build("KFunction") {
            build(function.name.toString())
            build(function.type.toString())
            build(function.bodyBlockExpression)
            build(function.annotationType.toString())
        }
    }

    override fun visitSFunction(function: SFunction) {
        build("SFunction") {
            build(function.name.toString())
            build(function.type.toString())
            build(function.bodyBlockExpression)
        }
    }

    override fun visitKProperty(property: KProperty) {
        build("KProperty") {
            build(property.name.toString())
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitSProperty(property: SProperty) {
        build("SProperty") {
            build(property.name.toString())
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitCTypeParameter(typeParameter: CTypeParameter) {
        build("CTypeParameter") {
            build(typeParameter.name.toString())
            build(typeParameter.type.toString())
        }
    }

    override fun visitCBlockExpression(blockExpression: CBlockExpression) {
        build("CBlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitCParenthesizedExpression(parenthesizedExpression: CParenthesizedExpression) {
        build("CParenthesizedExpression") {
            build(parenthesizedExpression.type.toString())
            build(parenthesizedExpression.expression)
        }
    }

    override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
        build("KBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitSBinaryExpression(binaryExpression: SBinaryExpression) {
        build("SBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitCReferenceExpression(referenceExpression: CReferenceExpression) {
        build("CReferenceExpression") {
            build(referenceExpression.type.toString())
            build(referenceExpression.reference.toString())
        }
    }

    override fun visitCCallExpression(callExpression: CCallExpression) {
        build("CCallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.toString())
            build(callExpression.valueArguments)
        }
    }

    override fun visitCValueArgument(valueArgument: CValueArgument) {
        build("CValueArgument") {
            build(valueArgument.reference.toString())
            build(valueArgument.expression)
        }
    }

    override fun visitCDotQualifiedExpression(dotQualifiedExpression: CDotQualifiedExpression) {
        build("CDotQualifiedExpression") {
            build(dotQualifiedExpression.type.toString())
            build(dotQualifiedExpression.receiver)
            build(dotQualifiedExpression.selector)
        }
    }

    override fun visitCConstantExpression(constantExpression: CConstantExpression) {
        build("CConstantExpression") {
            build(constantExpression.type.toString())
            build(constantExpression.value)
        }
    }

    override fun visitKFunctionLiteralExpression(functionLiteralExpression: KFunctionLiteralExpression) {
        build("KFunctionLiteralExpression") {
            build(functionLiteralExpression.type.toString())
            build(functionLiteralExpression.bodyBlockExpression)
        }
    }

    override fun visitKStringTemplateExpression(stringTemplateExpression: KStringTemplateExpression) {
        build("KStringTemplateExpression") {
            build(stringTemplateExpression.type.toString())
            build(stringTemplateExpression.entries)
        }
    }

    override fun visitKLiteralStringTemplateEntry(literalStringTemplateEntry: KLiteralStringTemplateEntry) {
        build("KLiteralStringTemplateEntry") {
            build(literalStringTemplateEntry.text)
        }
    }

    override fun visitKExpressionStringTemplateEntry(expressionStringTemplateEntry: KExpressionStringTemplateEntry) {
        build("KExpressionStringTemplateEntry") {
            build(expressionStringTemplateEntry.expression)
        }
    }

    override fun visitSStringExpression(stringExpression: SStringExpression) {
        build("SStringExpression") {
            build(stringExpression.type.toString())
            build(stringExpression.text)
        }
    }

    override fun visitSForeverStatement(foreverStatement: SForeverStatement) {
        build("SForeverStatement") {
            build(foreverStatement.type.toString())
            build(foreverStatement.bodyBlockExpression)
        }
    }

    private fun build(content: String) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: CElement?) {
        if (element != null)
            element.accept(this)
        else {
            if (!first) builder.append(", ")
            builder.append("null")
            first = false
        }
    }

    private fun build(name: String, content: () -> Unit) {
        if (!first) builder.append(", ")
        builder.append("$name(")
        first = true
        content()
        builder.append(")")
        first = false
    }

    private fun build(elements: List<CElement>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { it.accept(this) }
        builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: CElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}