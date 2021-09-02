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

import io.verik.compiler.ast.element.common.EAbstractBinaryExpression
import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EAbstractCallExpression
import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EAbstractEnumEntry
import io.verik.compiler.ast.element.common.EAbstractExpressionContainer
import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.EAbstractPackage
import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EAbstractReferenceExpression
import io.verik.compiler.ast.element.common.EAbstractStringEntryContainer
import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.EValueParameter
import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
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
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.ELoopStatement
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EProceduralBlock
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression

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

    open fun visitProject(project: EProject) {
        visitElement(project)
    }

    open fun visitAbstractPackage(abstractPackage: EAbstractPackage) {
        visitElement(abstractPackage)
    }

    open fun visitBasicPackage(basicPackage: EBasicPackage) {
        visitAbstractPackage(basicPackage)
    }

    open fun visitRootPackage(rootPackage: ERootPackage) {
        visitAbstractPackage(rootPackage)
    }

    open fun visitFile(file: EFile) {
        visitElement(file)
    }

//  CLASS  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractClass(abstractClass: EAbstractClass) {
        visitElement(abstractClass)
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

//  ENUM  //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitEnum(enum: EEnum) {
        visitElement(enum)
    }

//  FUNCTION  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
        visitElement(abstractFunction)
    }

    open fun visitKtFunction(function: EKtFunction) {
        visitAbstractFunction(function)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitAbstractFunction(function)
    }

//  PROCEDURAL BLOCK  //////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitProceduralBlock(proceduralBlock: EProceduralBlock) {
        visitElement(proceduralBlock)
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

//  PROPERTY  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        visitExpression(abstractProperty)
    }

    open fun visitKtProperty(property: EKtProperty) {
        visitAbstractProperty(property)
    }

    open fun visitSvProperty(property: ESvProperty) {
        visitAbstractProperty(property)
    }

    open fun visitAbstractEnumEntry(abstractEnumEntry: EAbstractEnumEntry) {
        visitAbstractProperty(abstractEnumEntry)
    }

    open fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
        visitAbstractEnumEntry(enumEntry)
    }

    open fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {
        visitAbstractEnumEntry(enumEntry)
    }

//  TYPE ALIAS  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitTypeAlias(typeAlias: ETypeAlias) {
        visitElement(typeAlias)
    }

//  TYPE PARAMETER  ////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitTypeParameter(typeParameter: ETypeParameter) {
        visitElement(typeParameter)
    }

//  VALUE PARAMETER  ///////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitValueParameter(valueParameter: EValueParameter) {
        visitElement(valueParameter)
    }

//  ANNOTATION  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAnnotation(annotation: EAnnotation) {
        visitElement(annotation)
    }

//  EXPRESSION  ////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    open fun visitAbstractReferenceExpression(abstractReferenceExpression: EAbstractReferenceExpression) {
        visitExpression(abstractReferenceExpression)
    }

    open fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
        visitAbstractReferenceExpression(referenceExpression)
    }

    open fun visitSvReferenceExpression(referenceExpression: ESvReferenceExpression) {
        visitAbstractReferenceExpression(referenceExpression)
    }

    open fun visitAbstractCallExpression(abstractCallExpression: EAbstractCallExpression) {
        visitExpression(abstractCallExpression)
    }

    open fun visitKtCallExpression(callExpression: EKtCallExpression) {
        visitAbstractCallExpression(callExpression)
    }

    open fun visitSvCallExpression(callExpression: ESvCallExpression) {
        visitAbstractCallExpression(callExpression)
    }

    open fun visitConstantExpression(constantExpression: EConstantExpression) {
        visitExpression(constantExpression)
    }

    open fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
        visitExpression(functionLiteralExpression)
    }

    open fun visitAbstractStringEntryContainer(abstractStringEntryContainer: EAbstractStringEntryContainer) {
        visitExpression(abstractStringEntryContainer)
    }

    open fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
        visitAbstractStringEntryContainer(stringTemplateExpression)
    }

    open fun visitInjectedExpression(injectedExpression: EInjectedExpression) {
        visitAbstractStringEntryContainer(injectedExpression)
    }

    open fun visitStringExpression(stringExpression: EStringExpression) {
        visitExpression(stringExpression)
    }

    open fun visitIfExpression(ifExpression: EIfExpression) {
        visitExpression(ifExpression)
    }

    open fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        visitExpression(inlineIfExpression)
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
