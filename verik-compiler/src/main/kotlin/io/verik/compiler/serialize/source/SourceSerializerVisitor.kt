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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EComponentInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EForkStatement
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedProperty
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
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvForStatement
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.element.sv.EWaitForkStatement
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages

class SourceSerializerVisitor(
    private val serializeContext: SerializeContext
) : Visitor() {

    private var firstDeclaration = true
    private var lastDeclarationIsProperty = false

    fun serialize(element: EElement) {
        serializeContext.label(element) {
            element.accept(this)
        }
    }

    fun serializeAsDeclaration(declaration: EDeclaration) {
        if (SerializerUtil.declarationIsHidden(declaration))
            return
        if (!firstDeclaration && !(lastDeclarationIsProperty && declaration is ESvProperty))
            serializeContext.appendLine()
        firstDeclaration = false
        lastDeclarationIsProperty = false
        serialize(declaration)
        lastDeclarationIsProperty = declaration is ESvProperty
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
        if (expression.serializationType == SerializationType.EXPRESSION) {
            serializeContext.label(expression) {
                serializeContext.appendLine(";")
            }
        }
    }

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitTypeDefinition(typeDefinition: ETypeDefinition) {
        DeclarationSerializer.serializeTypeDefinition(typeDefinition, serializeContext)
    }

    override fun visitInjectedProperty(injectedProperty: EInjectedProperty) {
        DeclarationSerializer.serializeInjectedProperty(injectedProperty, serializeContext)
    }

    override fun visitSvClass(`class`: ESvClass) {
        DeclarationSerializer.serializeSvClass(`class`, serializeContext)
    }

    override fun visitModule(module: EModule) {
        DeclarationSerializer.serializeModule(module, serializeContext)
    }

    override fun visitModuleInterface(moduleInterface: EModuleInterface) {
        DeclarationSerializer.serializeModuleInterface(moduleInterface, serializeContext)
    }

    override fun visitEnum(enum: EEnum) {
        DeclarationSerializer.serializeEnum(enum, serializeContext)
    }

    override fun visitStruct(struct: EStruct) {
        DeclarationSerializer.serializeStruct(struct, serializeContext)
    }

    override fun visitSvFunction(function: ESvFunction) {
        DeclarationSerializer.serializeSvFunction(function, serializeContext)
    }

    override fun visitTask(task: ETask) {
        DeclarationSerializer.serializeTask(task, serializeContext)
    }

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        DeclarationSerializer.serializeInitialBlock(initialBlock, serializeContext)
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        DeclarationSerializer.serializeAlwaysComBlock(alwaysComBlock, serializeContext)
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        DeclarationSerializer.serializeAlwaysSeqBlock(alwaysSeqBlock, serializeContext)
    }

    override fun visitSvProperty(property: ESvProperty) {
        DeclarationSerializer.serializeSvProperty(property, serializeContext)
    }

    override fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {
        DeclarationSerializer.serializeSvEnumEntry(enumEntry, serializeContext)
    }

    override fun visitComponentInstantiation(componentInstantiation: EComponentInstantiation) {
        DeclarationSerializer.serializeComponentInstantiation(componentInstantiation, serializeContext)
    }

    override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
        DeclarationSerializer.serializeModulePortInstantiation(modulePortInstantiation, serializeContext)
    }

    override fun visitClockingBlockInstantiation(clockingBlockInstantiation: EClockingBlockInstantiation) {
        DeclarationSerializer.serializeClockingBlockInstantiation(clockingBlockInstantiation, serializeContext)
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        DeclarationSerializer.serializeSvValueParameter(valueParameter, serializeContext)
    }

    override fun visitPort(port: EPort) {
        DeclarationSerializer.serializePort(port, serializeContext)
    }

    override fun visitBlockExpression(blockExpression: EBlockExpression) {
        ExpressionSerializer.serializeBlockExpression(blockExpression, serializeContext)
    }

    override fun visitPropertyStatement(propertyStatement: EPropertyStatement) {
        ExpressionSerializer.serializePropertyStatement(propertyStatement, serializeContext)
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        ExpressionSerializer.serializeParenthesizedExpression(parenthesizedExpression, serializeContext)
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        ExpressionSerializer.serializeSvUnaryExpression(unaryExpression, serializeContext)
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        ExpressionSerializer.serializeSvBinaryExpression(binaryExpression, serializeContext)
    }

    override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        ExpressionSerializer.serializeReferenceExpression(referenceExpression, serializeContext)
    }

    override fun visitCallExpression(callExpression: ECallExpression) {
        ExpressionSerializer.serializeCallExpression(callExpression, serializeContext)
    }

    override fun visitScopeExpression(scopeExpression: EScopeExpression) {
        ExpressionSerializer.serializeScopeExpression(scopeExpression, serializeContext)
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        ExpressionSerializer.serializeConstantExpression(constantExpression, serializeContext)
    }

    override fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
        ExpressionSerializer.serializeStructLiteralExpression(structLiteralExpression, serializeContext)
    }

    override fun visitNullExpression(nullExpression: ENullExpression) {
        ExpressionSerializer.serializeNullExpression(serializeContext)
    }

    override fun visitThisExpression(thisExpression: EThisExpression) {
        ExpressionSerializer.serializeThisExpression(serializeContext)
    }

    override fun visitSuperExpression(superExpression: ESuperExpression) {
        ExpressionSerializer.serializeSuperExpression(serializeContext)
    }

    override fun visitReturnStatement(returnStatement: EReturnStatement) {
        ExpressionSerializer.serializeReturnStatement(returnStatement, serializeContext)
    }

    override fun visitInjectedStatement(injectedStatement: EInjectedStatement) {
        ExpressionSerializer.serializeInjectedStatement(injectedStatement, serializeContext)
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        ExpressionSerializer.serializeStringExpression(stringExpression, serializeContext)
    }

    override fun visitSvArrayAccessExpression(arrayAccessExpression: ESvArrayAccessExpression) {
        ExpressionSerializer.serializeSvArrayAccessExpression(arrayAccessExpression, serializeContext)
    }

    override fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
        ExpressionSerializer.serializeConstantPartSelectExpression(constantPartSelectExpression, serializeContext)
    }

    override fun visitConcatenationExpression(concatenationExpression: EConcatenationExpression) {
        ExpressionSerializer.serializeConcatenationExpression(concatenationExpression, serializeContext)
    }

    override fun visitReplicationExpression(replicationExpression: EReplicationExpression) {
        ExpressionSerializer.serializeReplicationExpression(replicationExpression, serializeContext)
    }

    override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
        ExpressionSerializer.serializeStreamingExpression(streamingExpression, serializeContext)
    }

    override fun visitWidthCastExpression(widthCastExpression: EWidthCastExpression) {
        ExpressionSerializer.serializeWidthCastExpression(widthCastExpression, serializeContext)
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        ExpressionSerializer.serializeIfExpression(ifExpression, serializeContext)
    }

    override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        ExpressionSerializer.serializeInlineIfExpression(inlineIfExpression, serializeContext)
    }

    override fun visitImmediateAssertStatement(immediateAssertStatement: EImmediateAssertStatement) {
        ExpressionSerializer.serializeImmediateAssertStatement(immediateAssertStatement, serializeContext)
    }

    override fun visitCaseStatement(caseStatement: ECaseStatement) {
        ExpressionSerializer.serializeCaseStatement(caseStatement, serializeContext)
    }

    override fun visitWhileStatement(whileStatement: EWhileStatement) {
        ExpressionSerializer.serializeWhileStatement(whileStatement, serializeContext)
    }

    override fun visitSvForStatement(forStatement: ESvForStatement) {
        ExpressionSerializer.serializeForStatement(forStatement, serializeContext)
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        ExpressionSerializer.serializeForeverStatement(foreverStatement, serializeContext)
    }

    override fun visitRepeatStatement(repeatStatement: ERepeatStatement) {
        ExpressionSerializer.serializeRepeatStatement(repeatStatement, serializeContext)
    }

    override fun visitForkStatement(forkStatement: EForkStatement) {
        ExpressionSerializer.serializeForkStatement(forkStatement, serializeContext)
    }

    override fun visitWaitForkStatement(waitForkStatement: EWaitForkStatement) {
        ExpressionSerializer.serializeWaitForkStatement(serializeContext)
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        ExpressionSerializer.serializeEventExpression(eventExpression, serializeContext)
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        ExpressionSerializer.serializeEventControlExpression(eventControlExpression, serializeContext)
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        ExpressionSerializer.serializeDelayExpression(delayExpression, serializeContext)
    }
}
