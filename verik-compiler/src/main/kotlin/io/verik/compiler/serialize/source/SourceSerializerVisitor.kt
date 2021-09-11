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
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.SvSerializationType
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
        if (element is ESvEnumEntry)
            return
        if (!firstDeclaration && !(lastDeclarationIsProperty && element is ESvProperty))
            serializerContext.appendLine()
        firstDeclaration = false
        lastDeclarationIsProperty = false
        serialize(element)
        lastDeclarationIsProperty = element is ESvProperty
    }

    fun serializeAsExpression(expression: EExpression) {
        if (expression.serializationType != SvSerializationType.EXPRESSION)
            Messages.INTERNAL_ERROR.on(expression, "Expression expected but got: $expression")
        serialize(expression)
    }

    fun serializeAsStatement(expression: EExpression) {
        if (expression.serializationType !in listOf(SvSerializationType.EXPRESSION, SvSerializationType.STATEMENT))
            Messages.INTERNAL_ERROR.on(expression, "Expression or statement expected but got: $expression")
        serialize(expression)
        if (expression.serializationType == SvSerializationType.EXPRESSION)
            serializerContext.appendLine(";")
    }

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        DeclarationSerializer.serializeSvBasicClass(basicClass, serializerContext)
    }

    override fun visitModule(module: EModule) {
        DeclarationSerializer.serializeModule(module, serializerContext)
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

    override fun visitModuleInstantiation(moduleInstantiation: EModuleInstantiation) {
        DeclarationSerializer.serializeModuleInstantiation(moduleInstantiation, serializerContext)
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        DeclarationSerializer.serializeValueParameter(valueParameter, serializerContext)
    }

    override fun visitPort(port: EPort) {
        DeclarationSerializer.serializePort(port, serializerContext)
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        ExpressionSerializer.serializeSvBlockExpression(blockExpression, serializerContext)
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

    override fun visitSvReferenceExpression(referenceExpression: ESvReferenceExpression) {
        ExpressionSerializer.serializeSvReferenceExpression(referenceExpression, serializerContext)
    }

    override fun visitSvCallExpression(callExpression: ESvCallExpression) {
        ExpressionSerializer.serializeSvCallExpression(callExpression, serializerContext)
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        ExpressionSerializer.serializeConstantExpression(constantExpression, serializerContext)
    }

    override fun visitInjectedExpression(injectedExpression: EInjectedExpression) {
        ExpressionSerializer.serializeInjectedExpression(injectedExpression, serializerContext)
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        ExpressionSerializer.serializeStringExpression(stringExpression, serializerContext)
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        ExpressionSerializer.serializeIfExpression(ifExpression, serializerContext)
    }

    override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        ExpressionSerializer.serializeInlineIfExpression(inlineIfExpression, serializerContext)
    }

    override fun visitCaseStatement(caseStatement: ECaseStatement) {
        ExpressionSerializer.serializeCaseStatement(caseStatement, serializerContext)
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        ExpressionSerializer.serializeForeverStatement(foreverStatement, serializerContext)
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