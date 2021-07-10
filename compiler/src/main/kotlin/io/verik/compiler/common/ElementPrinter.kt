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
        build("File") {
            build(file.declarations)
        }
    }

    override fun visitKBasicClass(basicClass: KBasicClass) {
        build("KtClass") {
            build(basicClass.name.toString())
            build(basicClass.typeParameters)
            build(basicClass.declarations)
        }
    }

    override fun visitSBasicClass(basicClass: SBasicClass) {
        build("SvClass") {
            build(basicClass.name.toString())
            build(basicClass.typeParameters)
            build(basicClass.declarations)
        }
    }

    override fun visitSModule(module: SModule) {
        build("Module") {
            build(module.name.toString())
        }
    }

    override fun visitKFunction(function: KFunction) {
        build("KtFunction") {
            build(function.name.toString())
            build(function.type.toString())
            build(function.bodyBlockExpression)
            build(function.annotationType.toString())
        }
    }

    override fun visitSFunction(function: SFunction) {
        build("SvFunction") {
            build(function.name.toString())
            build(function.type.toString())
            build(function.bodyBlockExpression)
        }
    }

    override fun visitKProperty(property: KProperty) {
        build("KtProperty") {
            build(property.name.toString())
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitSProperty(property: SProperty) {
        build("SvProperty") {
            build(property.name.toString())
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitCTypeParameter(typeParameter: CTypeParameter) {
        build("TypeParameter") {
            build(typeParameter.name.toString())
            build(typeParameter.type.toString())
        }
    }

    override fun visitCBlockExpression(blockExpression: CBlockExpression) {
        build("BlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitCParenthesizedExpression(parenthesizedExpression: CParenthesizedExpression) {
        build("ParenthesizedExpression") {
            build(parenthesizedExpression.type.toString())
            build(parenthesizedExpression.expression)
        }
    }

    override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
        build("KtBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitSBinaryExpression(binaryExpression: SBinaryExpression) {
        build("SvBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitCReferenceExpression(referenceExpression: CReferenceExpression) {
        build("ReferenceExpression") {
            build(referenceExpression.type.toString())
            build(referenceExpression.reference.toString())
        }
    }

    override fun visitCCallExpression(callExpression: CCallExpression) {
        build("CallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.toString())
            build(callExpression.valueArguments)
        }
    }

    override fun visitCValueArgument(valueArgument: CValueArgument) {
        build("ValueArgument") {
            build(valueArgument.reference.toString())
            build(valueArgument.expression)
        }
    }

    override fun visitCDotQualifiedExpression(dotQualifiedExpression: CDotQualifiedExpression) {
        build("DotQualifiedExpression") {
            build(dotQualifiedExpression.type.toString())
            build(dotQualifiedExpression.receiver)
            build(dotQualifiedExpression.selector)
        }
    }

    override fun visitCConstantExpression(constantExpression: CConstantExpression) {
        build("ConstantExpression") {
            build(constantExpression.type.toString())
            build(constantExpression.value)
        }
    }

    override fun visitKFunctionLiteralExpression(functionLiteralExpression: KFunctionLiteralExpression) {
        build("FunctionLiteralExpression") {
            build(functionLiteralExpression.type.toString())
            build(functionLiteralExpression.bodyBlockExpression)
        }
    }

    override fun visitKStringTemplateExpression(stringTemplateExpression: KStringTemplateExpression) {
        build("StringTemplateExpression") {
            build(stringTemplateExpression.type.toString())
            build(stringTemplateExpression.entries)
        }
    }

    override fun visitKLiteralStringTemplateEntry(literalStringTemplateEntry: KLiteralStringTemplateEntry) {
        build("LiteralStringTemplateEntry") {
            build(literalStringTemplateEntry.text)
        }
    }

    override fun visitKExpressionStringTemplateEntry(expressionStringTemplateEntry: KExpressionStringTemplateEntry) {
        build("ExpressionStringTemplateEntry") {
            build(expressionStringTemplateEntry.expression)
        }
    }

    override fun visitSStringExpression(stringExpression: SStringExpression) {
        build("StringExpression") {
            build(stringExpression.type.toString())
            build(stringExpression.text)
        }
    }

    override fun visitSForeverExpression(foreverExpression: SForeverExpression) {
        build("SvForeverExpression") {
            build(foreverExpression.type.toString())
            build(foreverExpression.bodyBlockExpression)
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