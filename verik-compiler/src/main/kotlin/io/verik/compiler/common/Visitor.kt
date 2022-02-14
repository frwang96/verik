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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.declaration.common.EClassifier
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EInitializerBlock
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.declaration.sv.EAbstractComponent
import io.verik.compiler.ast.element.declaration.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAbstractProceduralBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.declaration.sv.EComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.EInitialBlock
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.declaration.sv.EPort
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.ESvAbstractFunction
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.declaration.sv.ETypeDefinition
import io.verik.compiler.ast.element.expression.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.expression.common.EAbstractBinaryExpression
import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EParenthesizedExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.EStringEntryExpression
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.ast.element.expression.common.EThisExpression
import io.verik.compiler.ast.element.expression.common.EWhileStatement
import io.verik.compiler.ast.element.expression.kt.EAsExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.element.expression.sv.ECaseStatement
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.EDelayExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.element.expression.sv.EEventExpression
import io.verik.compiler.ast.element.expression.sv.EForeverStatement
import io.verik.compiler.ast.element.expression.sv.EForkStatement
import io.verik.compiler.ast.element.expression.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.expression.sv.EInjectedStatement
import io.verik.compiler.ast.element.expression.sv.EInlineIfExpression
import io.verik.compiler.ast.element.expression.sv.ERepeatStatement
import io.verik.compiler.ast.element.expression.sv.EReplicationExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.ast.element.expression.sv.EStreamingExpression
import io.verik.compiler.ast.element.expression.sv.EStringExpression
import io.verik.compiler.ast.element.expression.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvForStatement
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.expression.sv.EWaitForkStatement
import io.verik.compiler.ast.element.expression.sv.EWidthCastExpression

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

    open fun visitProject(project: EProject) {
        visitElement(project)
    }

    open fun visitPackage(pkg: EPackage) {
        visitDeclaration(pkg)
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

// CLASS ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractClass(abstractClass: EAbstractClass) {
        visitClassifier(abstractClass)
    }

    open fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
        visitAbstractClass(abstractContainerClass)
    }

    open fun visitKtClass(cls: EKtClass) {
        visitAbstractContainerClass(cls)
    }

    open fun visitCompanionObject(companionObject: ECompanionObject) {
        visitAbstractContainerClass(companionObject)
    }

    open fun visitSvClass(cls: ESvClass) {
        visitAbstractContainerClass(cls)
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

// FUNCTION ////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    open fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
        visitKtAbstractFunction(secondaryConstructor)
    }

    open fun visitInitializerBlock(initializerBlock: EInitializerBlock) {
        visitAbstractFunction(initializerBlock)
    }

    open fun visitSvAbstractFunction(abstractFunction: ESvAbstractFunction) {
        visitAbstractFunction(abstractFunction)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitSvAbstractFunction(function)
    }

    open fun visitTask(task: ETask) {
        visitSvAbstractFunction(task)
    }

    open fun visitSvConstructor(constructor: ESvConstructor) {
        visitSvAbstractFunction(constructor)
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

// PROPERTY ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        visitDeclaration(abstractProperty)
    }

    open fun visitProperty(property: EProperty) {
        visitAbstractProperty(property)
    }

    open fun visitInjectedProperty(injectedProperty: EInjectedProperty) {
        visitAbstractProperty(injectedProperty)
    }

    open fun visitEnumEntry(enumEntry: EEnumEntry) {
        visitAbstractProperty(enumEntry)
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

// EXPRESSION //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitNothingExpression(nothingExpression: ENothingExpression) {
        visitExpression(nothingExpression)
    }

    open fun visitBlockExpression(blockExpression: EBlockExpression) {
        visitExpression(blockExpression)
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

    open fun visitCallExpression(callExpression: ECallExpression) {
        visitReceiverExpression(callExpression)
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

    open fun visitForkStatement(forkStatement: EForkStatement) {
        visitExpression(forkStatement)
    }

    open fun visitWaitForkStatement(waitForkStatement: EWaitForkStatement) {
        visitExpression(waitForkStatement)
    }

    open fun visitEventExpression(eventExpression: EEventExpression) {
        visitAbstractContainerExpression(eventExpression)
    }

    open fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        visitExpression(eventControlExpression)
    }

    open fun visitDelayExpression(delayExpression: EDelayExpression) {
        visitAbstractContainerExpression(delayExpression)
    }
}
