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

import io.verik.compiler.ast.element.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.common.EAbstractBinaryExpression
import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EAbstractCallExpression
import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EAbstractEnumEntry
import io.verik.compiler.ast.element.common.EAbstractExpressionContainer
import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.EAbstractInitializedProperty
import io.verik.compiler.ast.element.common.EAbstractPackage
import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EAbstractReferenceExpression
import io.verik.compiler.ast.element.common.EAbstractStringEntryContainer
import io.verik.compiler.ast.element.common.EAbstractValueParameter
import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EClassifier
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.ast.element.kt.EForExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtPropertyStatement
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.EAbstractComponent
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EAbstractProceduralBlock
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EComponentInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ERepeatStatement
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvPropertyStatement
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitNullElement(nullElement: EElement) {
        visitElement(nullElement)
    }

    open fun visitTypedElement(typedElement: ETypedElement) {
        visitElement(typedElement)
    }

    open fun visitExpression(expression: EExpression) {
        visitTypedElement(expression)
    }

    open fun visitNullExpression(nullExpression: ENullExpression) {
        visitExpression(nullExpression)
    }

    open fun visitAnnotation(annotation: EAnnotation) {
        visitElement(annotation)
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

    open fun visitClassifier(classifier: EClassifier) {
        visitTypedElement(classifier)
    }

    open fun visitTypeAlias(typeAlias: ETypeAlias) {
        visitClassifier(typeAlias)
    }

    open fun visitTypeParameter(typeParameter: ETypeParameter) {
        visitClassifier(typeParameter)
    }

//  CLASS  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractClass(abstractClass: EAbstractClass) {
        visitClassifier(abstractClass)
    }

    open fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
        visitAbstractClass(abstractContainerClass)
    }

    open fun visitKtBasicClass(basicClass: EKtBasicClass) {
        visitAbstractContainerClass(basicClass)
    }

    open fun visitSvBasicClass(basicClass: ESvBasicClass) {
        visitAbstractContainerClass(basicClass)
    }

    open fun visitAbstractComponent(abstractComponent: EAbstractComponent) {
        visitAbstractClass(abstractComponent)
    }

    open fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
        visitAbstractComponent(abstractContainerComponent)
    }

    open fun visitModule(module: EModule) {
        visitAbstractContainerComponent(module)
    }

    open fun visitModuleInterface(moduleInterface: EModuleInterface) {
        visitAbstractContainerComponent(moduleInterface)
    }

    open fun visitEnum(enum: EEnum) {
        visitAbstractClass(enum)
    }

    open fun visitStruct(struct: EStruct) {
        visitAbstractClass(struct)
    }

//  FUNCTION  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
        visitTypedElement(abstractFunction)
    }

    open fun visitKtAbstractFunction(abstractFunction: EKtAbstractFunction) {
        visitAbstractFunction(abstractFunction)
    }

    open fun visitKtFunction(function: EKtFunction) {
        visitKtAbstractFunction(function)
    }

    open fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        visitKtAbstractFunction(primaryConstructor)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitAbstractFunction(function)
    }

    open fun visitAbstractProceduralBlock(abstractProceduralBlock: EAbstractProceduralBlock) {
        visitAbstractFunction(abstractProceduralBlock)
    }

    open fun visitInitialBlock(initialBlock: EInitialBlock) {
        visitAbstractProceduralBlock(initialBlock)
    }

    open fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        visitAbstractProceduralBlock(alwaysComBlock)
    }

    open fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        visitAbstractProceduralBlock(alwaysSeqBlock)
    }

//  PROPERTY  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        visitTypedElement(abstractProperty)
    }

    open fun visitAbstractInitializedProperty(abstractInitializedProperty: EAbstractInitializedProperty) {
        visitAbstractProperty(abstractInitializedProperty)
    }

    open fun visitKtProperty(property: EKtProperty) {
        visitAbstractInitializedProperty(property)
    }

    open fun visitSvProperty(property: ESvProperty) {
        visitAbstractInitializedProperty(property)
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

    open fun visitComponentInstantiation(componentInstantiation: EComponentInstantiation) {
        visitAbstractProperty(componentInstantiation)
    }

    open fun visitAbstractValueParameter(abstractValueParameter: EAbstractValueParameter) {
        visitAbstractProperty(abstractValueParameter)
    }

    open fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        visitAbstractValueParameter(valueParameter)
    }

    open fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        visitAbstractValueParameter(valueParameter)
    }

    open fun visitPort(port: EPort) {
        visitAbstractValueParameter(port)
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

    open fun visitKtPropertyStatement(propertyStatement: EKtPropertyStatement) {
        visitExpression(propertyStatement)
    }

    open fun visitSvPropertyStatement(propertyStatement: ESvPropertyStatement) {
        visitExpression(propertyStatement)
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

    open fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
        visitExpression(structLiteralExpression)
    }

    open fun visitReturnStatement(returnStatement: EReturnStatement) {
        visitExpression(returnStatement)
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

    open fun visitAbstractArrayAccessExpression(abstractArrayAccessExpression: EAbstractArrayAccessExpression) {
        visitExpression(abstractArrayAccessExpression)
    }

    open fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
        visitAbstractArrayAccessExpression(arrayAccessExpression)
    }

    open fun visitSvArrayAccessExpression(arrayAccessExpression: ESvArrayAccessExpression) {
        visitAbstractArrayAccessExpression(arrayAccessExpression)
    }

    open fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
        visitAbstractArrayAccessExpression(constantPartSelectExpression)
    }

    open fun visitConcatenationExpression(concatenationExpression: EConcatenationExpression) {
        visitExpression(concatenationExpression)
    }

    open fun visitIfExpression(ifExpression: EIfExpression) {
        visitExpression(ifExpression)
    }

    open fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        visitExpression(inlineIfExpression)
    }

    open fun visitWhenExpression(whenExpression: EWhenExpression) {
        visitExpression(whenExpression)
    }

    open fun visitCaseStatement(caseStatement: ECaseStatement) {
        visitExpression(caseStatement)
    }

    open fun visitWhileExpression(whileExpression: EWhileExpression) {
        visitExpression(whileExpression)
    }

    open fun visitForExpression(forExpression: EForExpression) {
        visitExpression(forExpression)
    }

    open fun visitForStatement(forStatement: EForStatement) {
        visitExpression(forStatement)
    }

    open fun visitForeverStatement(foreverStatement: EForeverStatement) {
        visitExpression(foreverStatement)
    }

    open fun visitRepeatStatement(repeatStatement: ERepeatStatement) {
        visitExpression(repeatStatement)
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
