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

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.*

abstract class Visitor {

    open fun visitElement(element: VkElement) {}

    open fun visitFile(file: VkFile) {
        return visitElement(file)
    }

    open fun visitImportDirective(importDirective: VkImportDirective) {
        return visitElement(importDirective)
    }

    open fun visitExpression(expression: VkExpression) {
        return visitElement(expression)
    }

    open fun visitDeclaration(declaration: VkDeclaration) {
        return visitExpression(declaration)
    }

    open fun visitBaseClass(baseClass: VkBaseClass) {
        return visitDeclaration(baseClass)
    }

    open fun visitKtClass(ktClass: VkKtClass) {
        return visitBaseClass(ktClass)
    }

    open fun visitSvClass(svClass: VkSvClass) {
        return visitBaseClass(svClass)
    }

    open fun visitModule(module: VkModule) {
        return visitBaseClass(module)
    }

    open fun visitBaseFunction(baseFunction: VkBaseFunction) {
        return visitDeclaration(baseFunction)
    }

    open fun visitKtFunction(ktFunction: VkKtFunction) {
        return visitBaseFunction(ktFunction)
    }

    open fun visitSvFunction(svFunction: VkSvFunction) {
        return visitBaseFunction(svFunction)
    }

    open fun visitBaseProperty(baseProperty: VkBaseProperty) {
        return visitDeclaration(baseProperty)
    }

    open fun visitKtProperty(ktProperty: VkKtProperty) {
        return visitBaseProperty(ktProperty)
    }

    open fun visitSvProperty(svProperty: VkSvProperty) {
        return visitBaseProperty(svProperty)
    }

    open fun visitTypeParameter(typeParameter: VkTypeParameter) {
        return visitDeclaration(typeParameter)
    }

    open fun visitBlockExpression(blockExpression: VkBlockExpression) {
        return visitExpression(blockExpression)
    }

    open fun visitParenthesizedExpression(parenthesizedExpression: VkParenthesizedExpression) {
        return visitExpression(parenthesizedExpression)
    }

    open fun visitBinaryExpression(binaryExpression: VkBinaryExpression) {
        return visitExpression(binaryExpression)
    }

    open fun visitKtBinaryExpression(ktBinaryExpression: VkKtBinaryExpression) {
        return visitBinaryExpression(ktBinaryExpression)
    }

    open fun visitSvBinaryExpression(svBinaryExpression: VkSvBinaryExpression) {
        return visitBinaryExpression(svBinaryExpression)
    }

    open fun visitReferenceExpression(referenceExpression: VkReferenceExpression) {
        return visitExpression(referenceExpression)
    }

    open fun visitCallExpression(callExpression: VkCallExpression) {
        return visitExpression(callExpression)
    }

    open fun visitValueArgument(valueArgument: VkValueArgument) {
        return visitElement(valueArgument)
    }

    open fun visitDotQualifiedExpression(dotQualifiedExpression: VkDotQualifiedExpression) {
        return visitExpression(dotQualifiedExpression)
    }

    open fun visitConstantExpression(constantExpression: VkConstantExpression) {
        return visitExpression(constantExpression)
    }

    open fun visitLambdaExpression(lambdaExpression: VkLambdaExpression) {
        return visitExpression(lambdaExpression)
    }

    open fun visitStringTemplateExpression(stringTemplateExpression: VkStringTemplateExpression) {
        return visitExpression(stringTemplateExpression)
    }

    open fun visitStringTemplateEntry(stringTemplateEntry: VkStringTemplateEntry) {
        return visitElement(stringTemplateEntry)
    }

    open fun visitLiteralStringTemplateEntry(literalStringTemplateEntry: VkLiteralStringTemplateEntry) {
        return visitStringTemplateEntry(literalStringTemplateEntry)
    }

    open fun visitExpressionStringTemplateEntry(expressionStringTemplateEntry: VkExpressionStringTemplateEntry) {
        return visitStringTemplateEntry(expressionStringTemplateEntry)
    }

    open fun visitStringExpression(stringExpression: VkStringExpression) {
        return visitExpression(stringExpression)
    }
}