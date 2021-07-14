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

    open fun visitElement(element: EElement) {}

    open fun visitNullElement(nullElement: EElement) {
        visitElement(nullElement)
    }

    open fun visitExpression(expression: EExpression) {
        visitElement(expression)
    }

    open fun visitNullExpression(nullExpression: ENullExpression) {
        visitExpression(nullExpression)
    }

    open fun visitDeclaration(declaration: EDeclaration) {
        visitExpression(declaration)
    }

    open fun visitFile(file: EFile) {
        visitElement(file)
    }

    open fun visitImportDirective(importDirective: EImportDirective) {
        visitElement(importDirective)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractClass(abstractClass: EAbstractClass) {
        visitDeclaration(abstractClass)
    }

    open fun visitKtBasicClass(basicClass: EKtBasicClass) {
        visitAbstractClass(basicClass)
    }

    open fun visitSvBasicClass(basicClass: ESvBasicClass) {
        visitAbstractClass(basicClass)
    }

    open fun visitModule(module: EModule) {
        visitAbstractClass(module)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  FUNCTION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
        visitDeclaration(abstractFunction)
    }

    open fun visitKtFunction(function: EKtFunction) {
        visitAbstractFunction(function)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitAbstractFunction(function)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTY
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        visitDeclaration(abstractProperty)
    }

    open fun visitKtProperty(property: EKtProperty) {
        visitAbstractProperty(property)
    }

    open fun visitSvProperty(property: ESvProperty) {
        visitAbstractProperty(property)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  TYPE PARAMETER
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitTypeParameter(typeParameter: ETypeParameter) {
        visitDeclaration(typeParameter)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PROCEDURAL BLOCK
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitProceduralBlock(proceduralBlock: EProceduralBlock) {
        visitDeclaration(proceduralBlock)
    }

    open fun visitInitialBlock(initialBlock: EInitialBlock) {
        visitProceduralBlock(initialBlock)
    }

    open fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        visitProceduralBlock(alwaysComBlock)
    }

    open fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        visitProceduralBlock(alwaysSeqBlock)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  EXPRESSION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractBlockExpression(abstractBlockExpression: EAbstractBlockExpression) {
        visitExpression(abstractBlockExpression)
    }

    open fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
        visitAbstractBlockExpression(blockExpression)
    }

    open fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        visitAbstractBlockExpression(blockExpression)
    }

    open fun visitAbstractExpressionContainer(abstractExpressionContainer: EAbstractExpressionContainer) {
        visitExpression(abstractExpressionContainer)
    }

    open fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        visitAbstractExpressionContainer(parenthesizedExpression)
    }

    open fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
        visitAbstractExpressionContainer(unaryExpression)
    }

    open fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        visitAbstractExpressionContainer(unaryExpression)
    }

    open fun visitAbstractBinaryExpression(binaryExpression: EAbstractBinaryExpression) {
        visitExpression(binaryExpression)
    }

    open fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
        visitAbstractBinaryExpression(binaryExpression)
    }

    open fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        visitAbstractBinaryExpression(binaryExpression)
    }

    open fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        visitExpression(referenceExpression)
    }

    open fun visitCallExpression(callExpression: ECallExpression) {
        visitExpression(callExpression)
    }

    open fun visitTypeArgument(typeArgument: ETypeArgument) {
        visitElement(typeArgument)
    }

    open fun visitValueArgument(valueArgument: EValueArgument) {
        visitElement(valueArgument)
    }

    open fun visitDotQualifiedExpression(dotQualifiedExpression: EDotQualifiedExpression) {
        visitExpression(dotQualifiedExpression)
    }

    open fun visitConstantExpression(constantExpression: EConstantExpression) {
        visitExpression(constantExpression)
    }

    open fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
        visitExpression(functionLiteralExpression)
    }

    open fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
        visitExpression(stringTemplateExpression)
    }

    open fun visitStringTemplateEntry(stringTemplateEntry: EStringTemplateEntry) {
        visitElement(stringTemplateEntry)
    }

    open fun visitLiteralStringTemplateEntry(literalStringTemplateEntry: ELiteralStringTemplateEntry) {
        visitStringTemplateEntry(literalStringTemplateEntry)
    }

    open fun visitExpressionStringTemplateEntry(expressionStringTemplateEntry: EExpressionStringTemplateEntry) {
        visitStringTemplateEntry(expressionStringTemplateEntry)
    }

    open fun visitStringExpression(stringExpression: EStringExpression) {
        visitExpression(stringExpression)
    }

    open fun visitIfExpression(ifExpression: EIfExpression) {
        visitExpression(ifExpression)
    }

    open fun visitLoopStatement(loopStatement: ELoopStatement) {
        visitExpression(loopStatement)
    }

    open fun visitForeverStatement(foreverStatement: EForeverStatement) {
        visitLoopStatement(foreverStatement)
    }

    open fun visitEventExpression(eventExpression: EEventExpression) {
        visitAbstractExpressionContainer(eventExpression)
    }

    open fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        visitAbstractExpressionContainer(eventControlExpression)
    }

    open fun visitDelayExpression(delayExpression: EDelayExpression) {
        visitAbstractExpressionContainer(delayExpression)
    }
}