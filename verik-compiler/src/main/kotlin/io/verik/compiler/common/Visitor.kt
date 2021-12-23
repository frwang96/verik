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
import io.verik.compiler.ast.element.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.common.EAbstractEnumEntry
import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.EAbstractInitializedProperty
import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EAbstractValueParameter
import io.verik.compiler.ast.element.common.EClassifier
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EPackage
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EStringEntryExpression
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtForStatement
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.EAbstractComponent
import io.verik.compiler.ast.element.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EAbstractProceduralBlock
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EComponentInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedProperty
import io.verik.compiler.ast.element.sv.EInjectedStatement
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ERepeatStatement
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.ast.element.sv.EScopeExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvForStatement
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.element.sv.EWidthCastExpression

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitTypedElement(typedElement: ETypedElement) {
        visitElement(typedElement)
    }

    open fun visitDeclaration(declaration: EDeclaration) {
        visitTypedElement(declaration)
    }

    open fun visitExpression(expression: EExpression) {
        visitTypedElement(expression)
    }

    open fun visitNullExpression(nullExpression: ENullExpression) {
        visitExpression(nullExpression)
    }

    open fun visitProject(project: EProject) {
        visitDeclaration(project)
    }

    open fun visitPackage(`package`: EPackage) {
        visitDeclaration(`package`)
    }

    open fun visitFile(file: EFile) {
        visitDeclaration(file)
    }

    open fun visitClassifier(classifier: EClassifier) {
        visitDeclaration(classifier)
    }

    open fun visitTypeAlias(typeAlias: ETypeAlias) {
        visitClassifier(typeAlias)
    }

    open fun visitTypeDefinition(typeDefinition: ETypeDefinition) {
        visitClassifier(typeDefinition)
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

    open fun visitKtClass(`class`: EKtClass) {
        visitAbstractContainerClass(`class`)
    }

    open fun visitSvClass(`class`: ESvClass) {
        visitAbstractContainerClass(`class`)
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

    open fun visitModulePort(modulePort: EModulePort) {
        visitAbstractComponent(modulePort)
    }

    open fun visitClockingBlock(clockingBlock: EClockingBlock) {
        visitAbstractComponent(clockingBlock)
    }

    open fun visitEnum(enum: EEnum) {
        visitAbstractClass(enum)
    }

    open fun visitStruct(struct: EStruct) {
        visitAbstractClass(struct)
    }

//  FUNCTION  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
        visitDeclaration(abstractFunction)
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

    open fun visitKtConstructor(constructor: EKtConstructor) {
        visitKtAbstractFunction(constructor)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitAbstractFunction(function)
    }

    open fun visitTask(task: ETask) {
        visitAbstractFunction(task)
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
        visitDeclaration(abstractProperty)
    }

    open fun visitInjectedProperty(injectedProperty: EInjectedProperty) {
        visitAbstractProperty(injectedProperty)
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

    open fun visitAbstractComponentInstantiation(abstractComponentInstantiation: EAbstractComponentInstantiation) {
        visitAbstractProperty(abstractComponentInstantiation)
    }

    open fun visitComponentInstantiation(componentInstantiation: EComponentInstantiation) {
        visitAbstractComponentInstantiation(componentInstantiation)
    }

    open fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
        visitAbstractComponentInstantiation(modulePortInstantiation)
    }

    open fun visitClockingBlockInstantiation(clockingBlockInstantiation: EClockingBlockInstantiation) {
        visitAbstractComponentInstantiation(clockingBlockInstantiation)
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

    open fun visitAbstractContainerExpression(containerExpression: EAbstractContainerExpression) {
        visitExpression(containerExpression)
    }

    open fun visitPropertyStatement(propertyStatement: EPropertyStatement) {
        visitExpression(propertyStatement)
    }

    open fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        visitAbstractContainerExpression(parenthesizedExpression)
    }

    open fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
        visitAbstractContainerExpression(unaryExpression)
    }

    open fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        visitAbstractContainerExpression(unaryExpression)
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

    open fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
        visitExpression(receiverExpression)
    }

    open fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        visitReceiverExpression(referenceExpression)
    }

    open fun visitAbstractCallExpression(abstractCallExpression: EAbstractCallExpression) {
        visitReceiverExpression(abstractCallExpression)
    }

    open fun visitKtCallExpression(callExpression: EKtCallExpression) {
        visitAbstractCallExpression(callExpression)
    }

    open fun visitSvCallExpression(callExpression: ESvCallExpression) {
        visitAbstractCallExpression(callExpression)
    }

    open fun visitScopeExpression(scopeExpression: EScopeExpression) {
        visitExpression(scopeExpression)
    }

    open fun visitConstantExpression(constantExpression: EConstantExpression) {
        visitExpression(constantExpression)
    }

    open fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
        visitExpression(structLiteralExpression)
    }

    open fun visitThisExpression(thisExpression: EThisExpression) {
        visitExpression(thisExpression)
    }

    open fun visitSuperExpression(superExpression: ESuperExpression) {
        visitExpression(superExpression)
    }

    open fun visitReturnStatement(returnStatement: EReturnStatement) {
        visitExpression(returnStatement)
    }

    open fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
        visitExpression(functionLiteralExpression)
    }

    open fun visitStringEntryExpression(stringEntryExpression: EStringEntryExpression) {
        visitExpression(stringEntryExpression)
    }

    open fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
        visitStringEntryExpression(stringTemplateExpression)
    }

    open fun visitInjectedStatement(injectedStatement: EInjectedStatement) {
        visitStringEntryExpression(injectedStatement)
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

    open fun visitReplicationExpression(replicationExpression: EReplicationExpression) {
        visitAbstractContainerExpression(replicationExpression)
    }

    open fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
        visitAbstractContainerExpression(streamingExpression)
    }

    open fun visitIsExpression(isExpression: EIsExpression) {
        visitAbstractContainerExpression(isExpression)
    }

    open fun visitAsExpression(asExpression: EAsExpression) {
        visitAbstractContainerExpression(asExpression)
    }

    open fun visitWidthCastExpression(widthCastExpression: EWidthCastExpression) {
        visitAbstractContainerExpression(widthCastExpression)
    }

    open fun visitIfExpression(ifExpression: EIfExpression) {
        visitExpression(ifExpression)
    }

    open fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        visitExpression(inlineIfExpression)
    }

    open fun visitImmediateAssertStatement(immediateAssertStatement: EImmediateAssertStatement) {
        visitExpression(immediateAssertStatement)
    }

    open fun visitWhenExpression(whenExpression: EWhenExpression) {
        visitExpression(whenExpression)
    }

    open fun visitCaseStatement(caseStatement: ECaseStatement) {
        visitExpression(caseStatement)
    }

    open fun visitWhileStatement(whileStatement: EWhileStatement) {
        visitExpression(whileStatement)
    }

    open fun visitKtForStatement(forStatement: EKtForStatement) {
        visitExpression(forStatement)
    }

    open fun visitSvForStatement(forStatement: ESvForStatement) {
        visitExpression(forStatement)
    }

    open fun visitForeverStatement(foreverStatement: EForeverStatement) {
        visitExpression(foreverStatement)
    }

    open fun visitRepeatStatement(repeatStatement: ERepeatStatement) {
        visitExpression(repeatStatement)
    }

    open fun visitEventExpression(eventExpression: EEventExpression) {
        visitAbstractContainerExpression(eventExpression)
    }

    open fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        visitAbstractContainerExpression(eventControlExpression)
    }

    open fun visitDelayExpression(delayExpression: EDelayExpression) {
        visitAbstractContainerExpression(delayExpression)
    }
}
