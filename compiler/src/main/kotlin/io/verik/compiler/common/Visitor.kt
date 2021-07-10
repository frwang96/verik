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
        visitCElement(file)
    }

    open fun visitKImportDirective(importDirective: KImportDirective) {
        visitCElement(importDirective)
    }

    open fun visitCExpression(expression: CExpression) {
        visitCElement(expression)
    }

    open fun visitCDeclaration(declaration: CDeclaration) {
        visitCExpression(declaration)
    }

    open fun visitCAbstractClass(abstractClass: CAbstractClass) {
        visitCDeclaration(abstractClass)
    }

    open fun visitKBasicClass(basicClass: KBasicClass) {
        visitCAbstractClass(basicClass)
    }

    open fun visitSBasicClass(basicClass: SBasicClass) {
        visitCAbstractClass(basicClass)
    }

    open fun visitSModule(module: SModule) {
        visitCAbstractClass(module)
    }

    open fun visitCAbstractFunction(abstractFunction: CAbstractFunction) {
        visitCDeclaration(abstractFunction)
    }

    open fun visitKFunction(function: KFunction) {
        visitCAbstractFunction(function)
    }

    open fun visitSFunction(function: SFunction) {
        visitCAbstractFunction(function)
    }

    open fun visitCAbstractProperty(abstractProperty: CAbstractProperty) {
        visitCDeclaration(abstractProperty)
    }

    open fun visitKProperty(property: KProperty) {
        visitCAbstractProperty(property)
    }

    open fun visitSProperty(property: SProperty) {
        visitCAbstractProperty(property)
    }

    open fun visitCTypeParameter(typeParameter: CTypeParameter) {
        visitCDeclaration(typeParameter)
    }

    open fun visitSProceduralBlock(proceduralBlock: SProceduralBlock) {
        visitCDeclaration(proceduralBlock)
    }

    open fun visitSInitialBlock(initialBlock: SInitialBlock) {
        visitSProceduralBlock(initialBlock)
    }

    open fun visitCAbstractBlockExpression(abstractBlockExpression: CAbstractBlockExpression) {
        visitCExpression(abstractBlockExpression)
    }

    open fun visitKBlockExpression(blockExpression: KBlockExpression) {
        visitCAbstractBlockExpression(blockExpression)
    }

    open fun visitSBlockExpression(blockExpression: SBlockExpression) {
        visitCAbstractBlockExpression(blockExpression)
    }

    open fun visitCAbstractExpressionContainer(abstractExpressionContainer: CAbstractExpressionContainer) {
        visitCExpression(abstractExpressionContainer)
    }

    open fun visitCParenthesizedExpression(parenthesizedExpression: CParenthesizedExpression) {
        visitCAbstractExpressionContainer(parenthesizedExpression)
    }

    open fun visitCAbstractBinaryExpression(binaryExpression: CAbstractBinaryExpression) {
        visitCExpression(binaryExpression)
    }

    open fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
        visitCAbstractBinaryExpression(binaryExpression)
    }

    open fun visitSBinaryExpression(binaryExpression: SBinaryExpression) {
        visitCAbstractBinaryExpression(binaryExpression)
    }

    open fun visitCReferenceExpression(referenceExpression: CReferenceExpression) {
        visitCExpression(referenceExpression)
    }

    open fun visitCCallExpression(callExpression: CCallExpression) {
        visitCExpression(callExpression)
    }

    open fun visitCValueArgument(valueArgument: CValueArgument) {
        visitCElement(valueArgument)
    }

    open fun visitCDotQualifiedExpression(dotQualifiedExpression: CDotQualifiedExpression) {
        visitCExpression(dotQualifiedExpression)
    }

    open fun visitCConstantExpression(constantExpression: CConstantExpression) {
        visitCExpression(constantExpression)
    }

    open fun visitKFunctionLiteralExpression(functionLiteralExpression: KFunctionLiteralExpression) {
        visitCExpression(functionLiteralExpression)
    }

    open fun visitKStringTemplateExpression(stringTemplateExpression: KStringTemplateExpression) {
        visitCExpression(stringTemplateExpression)
    }

    open fun visitKStringTemplateEntry(stringTemplateEntry: KStringTemplateEntry) {
        visitCElement(stringTemplateEntry)
    }

    open fun visitKLiteralStringTemplateEntry(literalStringTemplateEntry: KLiteralStringTemplateEntry) {
        visitKStringTemplateEntry(literalStringTemplateEntry)
    }

    open fun visitKExpressionStringTemplateEntry(expressionStringTemplateEntry: KExpressionStringTemplateEntry) {
        visitKStringTemplateEntry(expressionStringTemplateEntry)
    }

    open fun visitSStringExpression(stringExpression: SStringExpression) {
        visitCExpression(stringExpression)
    }

    open fun visitSLoopStatement(loopStatement: SLoopStatement) {
        visitCExpression(loopStatement)
    }

    open fun visitSForeverStatement(foreverStatement: SForeverStatement) {
        visitSLoopStatement(foreverStatement)
    }

    open fun visitSEventExpression(eventExpression: SEventExpression) {
        visitCAbstractExpressionContainer(eventExpression)
    }

    open fun visitSEventControlExpression(eventControlExpression: SEventControlExpression) {
        visitCAbstractExpressionContainer(eventControlExpression)
    }
}