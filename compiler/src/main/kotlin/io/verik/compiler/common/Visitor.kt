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

abstract class Visitor {

    open fun visitCElement(element: CElement) {}

    open fun visitCFile(file: CFile) {
        return visitCElement(file)
    }

    open fun visitKImportDirective(importDirective: KImportDirective) {
        return visitCElement(importDirective)
    }

    open fun visitCExpression(expression: CExpression) {
        return visitCElement(expression)
    }

    open fun visitCDeclaration(declaration: CDeclaration) {
        return visitCExpression(declaration)
    }

    open fun visitCAbstractClass(abstractClass: CAbstractClass) {
        return visitCDeclaration(abstractClass)
    }

    open fun visitKBasicClass(basicClass: KBasicClass) {
        return visitCAbstractClass(basicClass)
    }

    open fun visitSBasicClass(basicClass: SBasicClass) {
        return visitCAbstractClass(basicClass)
    }

    open fun visitSModule(module: SModule) {
        return visitCAbstractClass(module)
    }

    open fun visitCAbstractFunction(abstractFunction: CAbstractFunction) {
        return visitCDeclaration(abstractFunction)
    }

    open fun visitKFunction(function: KFunction) {
        return visitCAbstractFunction(function)
    }

    open fun visitSFunction(function: SFunction) {
        return visitCAbstractFunction(function)
    }

    open fun visitCAbstractProperty(abstractProperty: CAbstractProperty) {
        return visitCDeclaration(abstractProperty)
    }

    open fun visitKProperty(property: KProperty) {
        return visitCAbstractProperty(property)
    }

    open fun visitSProperty(property: SProperty) {
        return visitCAbstractProperty(property)
    }

    open fun visitCTypeParameter(typeParameter: CTypeParameter) {
        return visitCDeclaration(typeParameter)
    }

    open fun visitCBlockExpression(blockExpression: CBlockExpression) {
        return visitCExpression(blockExpression)
    }

    open fun visitCParenthesizedExpression(parenthesizedExpression: CParenthesizedExpression) {
        return visitCExpression(parenthesizedExpression)
    }

    open fun visitCAbstractBinaryExpression(binaryExpression: CAbstractBinaryExpression) {
        return visitCExpression(binaryExpression)
    }

    open fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
        return visitCAbstractBinaryExpression(binaryExpression)
    }

    open fun visitSBinaryExpression(binaryExpression: SBinaryExpression) {
        return visitCAbstractBinaryExpression(binaryExpression)
    }

    open fun visitCReferenceExpression(referenceExpression: CReferenceExpression) {
        return visitCExpression(referenceExpression)
    }

    open fun visitCCallExpression(callExpression: CCallExpression) {
        return visitCExpression(callExpression)
    }

    open fun visitCValueArgument(valueArgument: CValueArgument) {
        return visitCElement(valueArgument)
    }

    open fun visitCDotQualifiedExpression(dotQualifiedExpression: CDotQualifiedExpression) {
        return visitCExpression(dotQualifiedExpression)
    }

    open fun visitCConstantExpression(constantExpression: CConstantExpression) {
        return visitCExpression(constantExpression)
    }

    open fun visitKFunctionLiteralExpression(functionLiteralExpression: KFunctionLiteralExpression) {
        return visitCExpression(functionLiteralExpression)
    }

    open fun visitKStringTemplateExpression(stringTemplateExpression: KStringTemplateExpression) {
        return visitCExpression(stringTemplateExpression)
    }

    open fun visitKStringTemplateEntry(stringTemplateEntry: KStringTemplateEntry) {
        return visitCElement(stringTemplateEntry)
    }

    open fun visitKLiteralStringTemplateEntry(literalStringTemplateEntry: KLiteralStringTemplateEntry) {
        return visitKStringTemplateEntry(literalStringTemplateEntry)
    }

    open fun visitKExpressionStringTemplateEntry(expressionStringTemplateEntry: KExpressionStringTemplateEntry) {
        return visitKStringTemplateEntry(expressionStringTemplateEntry)
    }

    open fun visitSStringExpression(stringExpression: SStringExpression) {
        return visitCExpression(stringExpression)
    }

    open fun visitSLoopStatement(loopStatement: SLoopStatement) {
        return visitCExpression(loopStatement)
    }

    open fun visitSForeverStatement(foreverStatement: SForeverStatement) {
        return visitSLoopStatement(foreverStatement)
    }
}