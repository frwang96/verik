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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EBasicComponentInstantiation
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedStatement
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
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
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvForStatement
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages

class SourceSerializerVisitor(private val serializerContext: SerializerContext) : Visitor() {

    private var firstDeclaration = true
    private var lastDeclarationIsProperty = false

    fun serialize(element: EElement) {
        serializerContext.label(element) {
            element.accept(this)
        }
    }

    fun serializeAsDeclaration(element: EElement) {
        if (element !is Declaration)
            Messages.INTERNAL_ERROR.on(element, "Declaration expected but got: $element")
        if (SerializerUtil.declarationIsHidden(element))
            return
        if (!firstDeclaration && !(lastDeclarationIsProperty && element is ESvProperty))
            serializerContext.appendLine()
        firstDeclaration = false
        lastDeclarationIsProperty = false
        serialize(element)
        lastDeclarationIsProperty = element is ESvProperty
    }

    fun serializeAsExpression(expression: EExpression) {
        if (expression.serializationType != SerializationType.EXPRESSION)
            Messages.INTERNAL_ERROR.on(expression, "Expression expected but got: $expression")
        serialize(expression)
    }

    fun serializeAsStatement(expression: EExpression) {
        if (expression.serializationType == SerializationType.INTERNAL)
            Messages.INTERNAL_ERROR.on(expression, "Expression or statement expected but got: $expression")
        serialize(expression)
        if (expression.serializationType == SerializationType.EXPRESSION)
            serializerContext.appendLine(";")
    }

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitTypeDefinition(typeDefinition: ETypeDefinition) {
        DeclarationSerializer.serializeTypeDefinition(typeDefinition, serializerContext)
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        DeclarationSerializer.serializeSvBasicClass(basicClass, serializerContext)
    }

    override fun visitModule(module: EModule) {
        DeclarationSerializer.serializeModule(module, serializerContext)
    }

    override fun visitModuleInterface(moduleInterface: EModuleInterface) {
        DeclarationSerializer.serializeModuleInterface(moduleInterface, serializerContext)
    }

    override fun visitEnum(enum: EEnum) {
        DeclarationSerializer.serializeEnum(enum, serializerContext)
    }

    override fun visitStruct(struct: EStruct) {
        DeclarationSerializer.serializeStruct(struct, serializerContext)
    }

    override fun visitSvFunction(function: ESvFunction) {
        DeclarationSerializer.serializeSvFunction(function, serializerContext)
    }

    override fun visitTask(task: ETask) {
        DeclarationSerializer.serializeTask(task, serializerContext)
    }

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        DeclarationSerializer.serializeInitialBlock(initialBlock, serializerContext)
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        DeclarationSerializer.serializeAlwaysComBlock(alwaysComBlock, serializerContext)
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        DeclarationSerializer.serializeAlwaysSeqBlock(alwaysSeqBlock, serializerContext)
    }

    override fun visitSvProperty(property: ESvProperty) {
        DeclarationSerializer.serializeSvProperty(property, serializerContext)
    }

    override fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {
        DeclarationSerializer.serializeSvEnumEntry(enumEntry, serializerContext)
    }

    override fun visitBasicComponentInstantiation(basicComponentInstantiation: EBasicComponentInstantiation) {
        DeclarationSerializer.serializeBasicComponentInstantiation(basicComponentInstantiation, serializerContext)
    }

    override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
        DeclarationSerializer.serializeModulePortInstantiation(modulePortInstantiation, serializerContext)
    }

    override fun visitClockingBlockInstantiation(clockingBlockInstantiation: EClockingBlockInstantiation) {
        DeclarationSerializer.serializeClockingBlockInstantiation(clockingBlockInstantiation, serializerContext)
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        DeclarationSerializer.serializeSvValueParameter(valueParameter, serializerContext)
    }

    override fun visitPort(port: EPort) {
        DeclarationSerializer.serializePort(port, serializerContext)
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        ExpressionSerializer.serializeSvBlockExpression(blockExpression, serializerContext)
    }

    override fun visitPropertyStatement(propertyStatement: EPropertyStatement) {
        ExpressionSerializer.serializePropertyStatement(propertyStatement, serializerContext)
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        ExpressionSerializer.serializeParenthesizedExpression(parenthesizedExpression, serializerContext)
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        ExpressionSerializer.serializeSvUnaryExpression(unaryExpression, serializerContext)
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        ExpressionSerializer.serializeSvBinaryExpression(binaryExpression, serializerContext)
    }

    override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        ExpressionSerializer.serializeReferenceExpression(referenceExpression, serializerContext)
    }

    override fun visitSvCallExpression(callExpression: ESvCallExpression) {
        ExpressionSerializer.serializeSvCallExpression(callExpression, serializerContext)
    }

    override fun visitScopeExpression(scopeExpression: EScopeExpression) {
        ExpressionSerializer.serializeScopeExpression(scopeExpression, serializerContext)
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        ExpressionSerializer.serializeConstantExpression(constantExpression, serializerContext)
    }

    override fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
        ExpressionSerializer.serializeStructLiteralExpression(structLiteralExpression, serializerContext)
    }

    override fun visitThisExpression(thisExpression: EThisExpression) {
        ExpressionSerializer.serializeThisExpression(serializerContext)
    }

    override fun visitSuperExpression(superExpression: ESuperExpression) {
        ExpressionSerializer.serializeSuperExpression(serializerContext)
    }

    override fun visitReturnStatement(returnStatement: EReturnStatement) {
        ExpressionSerializer.serializeReturnStatement(returnStatement, serializerContext)
    }

    override fun visitInjectedStatement(injectedStatement: EInjectedStatement) {
        ExpressionSerializer.serializeInjectedStatement(injectedStatement, serializerContext)
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        ExpressionSerializer.serializeStringExpression(stringExpression, serializerContext)
    }

    override fun visitSvArrayAccessExpression(arrayAccessExpression: ESvArrayAccessExpression) {
        ExpressionSerializer.serializeSvArrayAccessExpression(arrayAccessExpression, serializerContext)
    }

    override fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
        ExpressionSerializer.serializeConstantPartSelectExpression(constantPartSelectExpression, serializerContext)
    }

    override fun visitConcatenationExpression(concatenationExpression: EConcatenationExpression) {
        ExpressionSerializer.serializeConcatenationExpression(concatenationExpression, serializerContext)
    }

    override fun visitReplicationExpression(replicationExpression: EReplicationExpression) {
        ExpressionSerializer.serializeReplicationExpression(replicationExpression, serializerContext)
    }

    override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
        ExpressionSerializer.serializeStreamingExpression(streamingExpression, serializerContext)
    }

    override fun visitWidthCastExpression(widthCastExpression: EWidthCastExpression) {
        ExpressionSerializer.serializeWidthCastExpression(widthCastExpression, serializerContext)
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        ExpressionSerializer.serializeIfExpression(ifExpression, serializerContext)
    }

    override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        ExpressionSerializer.serializeInlineIfExpression(inlineIfExpression, serializerContext)
    }

    override fun visitImmediateAssertStatement(immediateAssertStatement: EImmediateAssertStatement) {
        ExpressionSerializer.serializeImmediateAssertStatement(immediateAssertStatement, serializerContext)
    }

    override fun visitCaseStatement(caseStatement: ECaseStatement) {
        ExpressionSerializer.serializeCaseStatement(caseStatement, serializerContext)
    }

    override fun visitWhileStatement(whileStatement: EWhileStatement) {
        ExpressionSerializer.serializeWhileStatement(whileStatement, serializerContext)
    }

    override fun visitSvForStatement(forStatement: ESvForStatement) {
        ExpressionSerializer.serializeForStatement(forStatement, serializerContext)
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        ExpressionSerializer.serializeForeverStatement(foreverStatement, serializerContext)
    }

    override fun visitRepeatStatement(repeatStatement: ERepeatStatement) {
        ExpressionSerializer.serializeRepeatStatement(repeatStatement, serializerContext)
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        ExpressionSerializer.serializeEventExpression(eventExpression, serializerContext)
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        ExpressionSerializer.serializeEventControlExpression(eventControlExpression, serializerContext)
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        ExpressionSerializer.serializeDelayExpression(delayExpression, serializerContext)
    }
}
