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

    override fun visitNullElement(nullElement: EElement) {
        build("NullElement") {}
    }

    override fun visitNullExpression(nullExpression: ENullExpression) {
        build("NullExpression") {}
    }

    override fun visitFile(file: EFile) {
        build("File") {
            build(file.members)
        }
    }

    override fun visitKtBasicClass(basicClass: EKtBasicClass) {
        build("KtBasicClass") {
            build(basicClass.name)
            build(basicClass.typeParameters)
            build(basicClass.members)
        }
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        build("SvBasicClass") {
            build(basicClass.name)
            build(basicClass.typeParameters)
            build(basicClass.members)
        }
    }

    override fun visitModule(module: EModule) {
        build("Module") {
            build(module.name)
        }
    }

    override fun visitKtFunction(function: EKtFunction) {
        build("KtFunction") {
            build(function.name)
            build(function.returnType.toString())
            build(function.body)
            build(function.annotationType.toString())
        }
    }

    override fun visitSvFunction(function: ESvFunction) {
        build("SvFunction") {
            build(function.name)
            build(function.returnType.toString())
            build(function.body)
        }
    }

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        build("InitialBlock") {
            build(initialBlock.name)
            build(initialBlock.body)
        }
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        build("AlwaysComBlock") {
            build(alwaysComBlock.name)
            build(alwaysComBlock.body)
        }
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        build("AlwaysSeqBlock") {
            build(alwaysSeqBlock.name)
            build(alwaysSeqBlock.eventControlExpression)
            build(alwaysSeqBlock.body)
        }
    }

    override fun visitKtProperty(property: EKtProperty) {
        build("KtProperty") {
            build(property.name)
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitSvProperty(property: ESvProperty) {
        build("SvProperty") {
            build(property.name)
            build(property.type.toString())
            build(property.initializer)
        }
    }

    override fun visitTypeParameter(typeParameter: ETypeParameter) {
        build("TypeParameter") {
            build(typeParameter.name)
            build(typeParameter.typeConstraint.toString())
        }
    }

    override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
        build("KtBlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        build("SvBlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.decorated.toString())
            build(blockExpression.name.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        build("ParenthesizedExpression") {
            build(parenthesizedExpression.type.toString())
            build(parenthesizedExpression.expression)
        }
    }

    override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
        build("KtUnaryExpression") {
            build(unaryExpression.type.toString())
            build(unaryExpression.kind.toString())
            build(unaryExpression.expression)
        }
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        build("SvUnaryExpression") {
            build(unaryExpression.type.toString())
            build(unaryExpression.kind.toString())
            build(unaryExpression.expression)
        }
    }

    override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
        build("KtBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        build("SvBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.kind.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
        }
    }

    override fun visitSimpleNameExpression(simpleNameExpression: ESimpleNameExpression) {
        build("SimpleNameExpression") {
            build(simpleNameExpression.type.toString())
            build(simpleNameExpression.reference.name)
            build(simpleNameExpression.receiver)
        }
    }

    override fun visitCallExpression(callExpression: ECallExpression) {
        build("CallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.name)
            build(callExpression.receiver)
            build(callExpression.typeArguments)
            build(callExpression.valueArguments)
        }
    }

    override fun visitTypeArgument(typeArgument: ETypeArgument) {
        build("TypeArgument") {
            build(typeArgument.reference.name)
            build(typeArgument.type.toString())
        }
    }

    override fun visitValueArgument(valueArgument: EValueArgument) {
        build("ValueArgument") {
            build(valueArgument.reference.name)
            build(valueArgument.expression)
        }
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        build("ConstantExpression") {
            build(constantExpression.type.toString())
            build(constantExpression.value)
        }
    }

    override fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
        build("FunctionLiteralExpression") {
            build(functionLiteralExpression.type.toString())
            build(functionLiteralExpression.body)
        }
    }

    override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
        build("StringTemplateExpression") {
            build(stringTemplateExpression.type.toString())
            build(stringTemplateExpression.entries)
        }
    }

    override fun visitLiteralStringTemplateEntry(literalStringTemplateEntry: ELiteralStringTemplateEntry) {
        build("LiteralStringTemplateEntry") {
            build(literalStringTemplateEntry.text)
        }
    }

    override fun visitExpressionStringTemplateEntry(expressionStringTemplateEntry: EExpressionStringTemplateEntry) {
        build("ExpressionStringTemplateEntry") {
            build(expressionStringTemplateEntry.expression)
        }
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        build("StringExpression") {
            build(stringExpression.type.toString())
            build(stringExpression.text)
        }
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        build("IfExpression") {
            build(ifExpression.type.toString())
            build(ifExpression.condition)
            build(ifExpression.thenExpression)
            build(ifExpression.elseExpression)
        }
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        build("ForeverStatement") {
            build(foreverStatement.type.toString())
            build(foreverStatement.body)
        }
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        build("EdgeExpression") {
            build(eventExpression.type.toString())
            build(eventExpression.edgeType.toString())
            build(eventExpression.expression)
        }
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        build("EventControlExpression") {
            build(eventControlExpression.type.toString())
            build(eventControlExpression.expression)
        }
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        build("DelayExpression") {
            build(delayExpression.type.toString())
            build(delayExpression.expression)
        }
    }

    private fun build(content: String) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: EElement?) {
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

    private fun build(elements: List<Any>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { if (it is EElement) it.accept(this) }
        builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: EElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}