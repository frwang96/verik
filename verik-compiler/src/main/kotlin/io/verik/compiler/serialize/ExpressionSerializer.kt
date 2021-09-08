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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry

object ExpressionSerializer {

    fun serializeSvBlockExpression(
        blockExpression: ESvBlockExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        if (blockExpression.decorated) {
            sourceSerializerContext.append("begin")
            if (blockExpression.name != null)
                sourceSerializerContext.append(" : ${blockExpression.name}")
            sourceSerializerContext.appendLine()
            sourceSerializerContext.indent {
                blockExpression.statements.forEach { sourceSerializerContext.serializeAsStatement(it) }
            }
            sourceSerializerContext.append("end")
            if (blockExpression.name != null)
                sourceSerializerContext.append(" : ${blockExpression.name}")
            sourceSerializerContext.appendLine()
        } else {
            blockExpression.statements.forEach { sourceSerializerContext.serializeAsStatement(it) }
        }
    }

    fun serializeParenthesizedExpression(
        parenthesizedExpression: EParenthesizedExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append("(")
        sourceSerializerContext.serializeAsExpression(parenthesizedExpression.expression)
        sourceSerializerContext.append(")")
    }

    fun serializeSvUnaryExpression(
        unaryExpression: ESvUnaryExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append(unaryExpression.kind.serialize())
        sourceSerializerContext.serializeAsExpression(unaryExpression.expression)
    }

    fun serializeSvBinaryExpression(
        binaryExpression: ESvBinaryExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.serializeAsExpression(binaryExpression.left)
        sourceSerializerContext.hardBreak()
        sourceSerializerContext.append(binaryExpression.kind.serialize())
        sourceSerializerContext.append(" ")
        sourceSerializerContext.serializeAsExpression(binaryExpression.right)
    }

    fun serializeSvReferenceExpression(
        referenceExpression: ESvReferenceExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        val receiver = referenceExpression.receiver
        if (receiver != null) {
            sourceSerializerContext.serializeAsExpression(receiver)
            sourceSerializerContext.append(if (referenceExpression.isScopeResolution) "::" else ".")
        }
        sourceSerializerContext.append(referenceExpression.reference.name)
    }

    fun serializeSvCallExpression(callExpression: ESvCallExpression, sourceSerializerContext: SourceSerializerContext) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            sourceSerializerContext.serializeAsExpression(receiver)
            sourceSerializerContext.append(if (callExpression.isScopeResolution) "::" else ".")
        }
        sourceSerializerContext.append(callExpression.reference.name)
        sourceSerializerContext.append("(")
        if (callExpression.valueArguments.isNotEmpty()) {
            sourceSerializerContext.softBreak()
            sourceSerializerContext.join(callExpression.valueArguments) {
                sourceSerializerContext.serializeAsExpression(it)
            }
        }
        sourceSerializerContext.append(")")
    }

    fun serializeConstantExpression(
        constantExpression: EConstantExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append(constantExpression.value)
    }

    fun serializeInjectedExpression(
        injectedExpression: EInjectedExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        injectedExpression.entries.forEach {
            when (it) {
                is LiteralStringEntry ->
                    sourceSerializerContext.append(it.text)
                is ExpressionStringEntry ->
                    sourceSerializerContext.serializeAsExpression(it.expression)
            }
        }
    }

    fun serializeStringExpression(
        stringExpression: EStringExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append("\"${stringExpression.text}\"")
    }

    fun serializeIfExpression(ifExpression: EIfExpression, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append("if (")
        sourceSerializerContext.serializeAsExpression(ifExpression.condition)
        sourceSerializerContext.append(")")
        val thenExpression = ifExpression.thenExpression
        if (thenExpression != null) {
            if (thenExpression is ESvBlockExpression) {
                sourceSerializerContext.append(" ")
                sourceSerializerContext.serializeAsStatement(thenExpression)
            } else {
                sourceSerializerContext.appendLine()
                sourceSerializerContext.indent {
                    sourceSerializerContext.serializeAsStatement(thenExpression)
                }
            }
        } else {
            sourceSerializerContext.appendLine(";")
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            if (elseExpression is ESvBlockExpression || elseExpression is EIfExpression) {
                sourceSerializerContext.append("else ")
                sourceSerializerContext.serializeAsStatement(elseExpression)
            } else {
                sourceSerializerContext.appendLine("else")
                sourceSerializerContext.indent {
                    sourceSerializerContext.serializeAsStatement(elseExpression)
                }
            }
        }
    }

    fun serializeInlineIfExpression(
        inlineIfExpression: EInlineIfExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.serializeAsExpression(inlineIfExpression.condition)
        sourceSerializerContext.hardBreak()
        sourceSerializerContext.append("? ")
        sourceSerializerContext.serializeAsExpression(inlineIfExpression.thenExpression)
        sourceSerializerContext.hardBreak()
        sourceSerializerContext.append(": ")
        sourceSerializerContext.serializeAsExpression(inlineIfExpression.elseExpression)
    }

    fun serializeCaseStatement(
        caseStatement: ECaseStatement,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append("case (")
        sourceSerializerContext.serializeAsExpression(caseStatement.subject)
        sourceSerializerContext.appendLine(")")
        sourceSerializerContext.indent {
            caseStatement.entries.forEach { entry ->
                if (entry.conditions.isNotEmpty()) {
                    sourceSerializerContext.join(entry.conditions) {
                        sourceSerializerContext.serializeAsExpression(it)
                    }
                } else {
                    sourceSerializerContext.append("default")
                }
                sourceSerializerContext.append(" : ")
                sourceSerializerContext.serializeAsStatement(entry.body)
            }
        }
        sourceSerializerContext.appendLine("endcase")
    }

    fun serializeForeverStatement(
        foreverStatement: EForeverStatement,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append("forever ")
        sourceSerializerContext.serializeAsStatement(foreverStatement.body)
    }

    fun serializeEventExpression(
        eventExpression: EEventExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        when (eventExpression.edgeType) {
            EdgeType.POSEDGE -> sourceSerializerContext.append("posedge ")
            EdgeType.NEGEDGE -> sourceSerializerContext.append("negedge ")
            EdgeType.EDGE -> sourceSerializerContext.append("edge ")
        }
        sourceSerializerContext.serializeAsExpression(eventExpression.expression)
    }

    fun serializeEventControlExpression(
        eventControlExpression: EEventControlExpression,
        sourceSerializerContext: SourceSerializerContext
    ) {
        sourceSerializerContext.append("@")
        sourceSerializerContext.serializeAsExpression(eventControlExpression.expression)
    }

    fun serializeDelayExpression(delayExpression: EDelayExpression, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append("#")
        sourceSerializerContext.serializeAsExpression(delayExpression.expression)
    }
}
