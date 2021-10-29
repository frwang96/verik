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
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.ERepeatStatement
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.ast.element.sv.EScopeExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry

object ExpressionSerializer {

    fun serializeSvBlockExpression(blockExpression: ESvBlockExpression, serializerContext: SerializerContext) {
        if (blockExpression.decorated) {
            serializerContext.append("begin")
            if (blockExpression.name != null)
                serializerContext.append(" : ${blockExpression.name}")
            serializerContext.appendLine()
            serializerContext.indent {
                blockExpression.statements.forEach { serializerContext.serializeAsStatement(it) }
            }
            serializerContext.append("end")
            if (blockExpression.name != null)
                serializerContext.append(" : ${blockExpression.name}")
            serializerContext.appendLine()
        } else {
            blockExpression.statements.forEach { serializerContext.serializeAsStatement(it) }
        }
    }

    fun serializePropertyStatement(propertyStatement: EPropertyStatement, serializerContext: SerializerContext) {
        serializerContext.serialize(propertyStatement.property)
    }

    fun serializeParenthesizedExpression(
        parenthesizedExpression: EParenthesizedExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("(")
        serializerContext.serializeAsExpression(parenthesizedExpression.expression)
        serializerContext.append(")")
    }

    fun serializeSvUnaryExpression(unaryExpression: ESvUnaryExpression, serializerContext: SerializerContext) {
        val prefix = unaryExpression.kind.serializePrefix()
        if (prefix != null)
            serializerContext.append(prefix)
        serializerContext.serializeAsExpression(unaryExpression.expression)
        val postfix = unaryExpression.kind.serializePostfix()
        if (postfix != null)
            serializerContext.append(postfix)
    }

    fun serializeSvBinaryExpression(binaryExpression: ESvBinaryExpression, serializerContext: SerializerContext) {
        serializerContext.serializeAsExpression(binaryExpression.left)
        serializerContext.hardBreak()
        serializerContext.append(binaryExpression.kind.serialize())
        serializerContext.append(" ")
        serializerContext.serializeAsExpression(binaryExpression.right)
    }

    fun serializeSvReferenceExpression(
        referenceExpression: ESvReferenceExpression,
        serializerContext: SerializerContext
    ) {
        val receiver = referenceExpression.receiver
        if (receiver != null) {
            serializerContext.serializeAsExpression(receiver)
            serializerContext.append(if (referenceExpression.isScopeResolution) "::" else ".")
        }
        serializerContext.append(referenceExpression.reference.name)
    }

    fun serializeSvCallExpression(callExpression: ESvCallExpression, serializerContext: SerializerContext) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            serializerContext.serializeAsExpression(receiver)
            serializerContext.append(if (callExpression.isScopeResolution) "::" else ".")
        }
        serializerContext.append(callExpression.reference.name)
        serializerContext.append("(")
        if (callExpression.valueArguments.isNotEmpty()) {
            serializerContext.softBreak()
            serializerContext.join(callExpression.valueArguments) {
                serializerContext.serializeAsExpression(it)
            }
        }
        serializerContext.append(")")
    }

    fun serializeScopeExpression(scopeExpression: EScopeExpression, serializerContext: SerializerContext) {
        val serializedType = TypeSerializer.serialize(scopeExpression.scope, scopeExpression)
        serializedType.checkNoPackedDimension(scopeExpression)
        serializedType.checkNoUnpackedDimension(scopeExpression)
        serializerContext.append(serializedType.base)
    }

    fun serializeConstantExpression(constantExpression: EConstantExpression, serializerContext: SerializerContext) {
        serializerContext.append(constantExpression.value)
    }

    fun serializeStructLiteralExpression(
        structLiteralExpression: EStructLiteralExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("'{")
        serializerContext.join(structLiteralExpression.entries) {
            serializerContext.append("${it.reference.name}:")
            serializerContext.serializeAsExpression(it.expression)
        }
        serializerContext.append("}")
    }

    fun serializeThisExpression(serializerContext: SerializerContext) {
        serializerContext.append("this")
    }

    fun serializeSuperExpression(serializerContext: SerializerContext) {
        serializerContext.append("super")
    }

    fun serializeReturnStatement(returnStatement: EReturnStatement, serializerContext: SerializerContext) {
        val expression = returnStatement.expression
        if (expression == null) {
            serializerContext.appendLine("return;")
        } else {
            serializerContext.append("return ")
            serializerContext.serializeAsExpression(expression)
            serializerContext.appendLine(";")
        }
    }

    fun serializeInjectedExpression(injectedExpression: EInjectedExpression, serializerContext: SerializerContext) {
        injectedExpression.entries.forEach {
            when (it) {
                is LiteralStringEntry ->
                    serializerContext.append(it.text)
                is ExpressionStringEntry ->
                    serializerContext.serializeAsExpression(it.expression)
            }
        }
    }

    fun serializeStringExpression(stringExpression: EStringExpression, serializerContext: SerializerContext) {
        serializerContext.append("\"${stringExpression.text}\"")
    }

    fun serializeSvArrayAccessExpression(
        arrayAccessExpression: ESvArrayAccessExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.serializeAsExpression(arrayAccessExpression.array)
        serializerContext.append("[")
        serializerContext.serializeAsExpression(arrayAccessExpression.index)
        serializerContext.append("]")
    }

    fun serializeConstantPartSelectExpression(
        constantPartSelectExpression: EConstantPartSelectExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.serializeAsExpression(constantPartSelectExpression.array)
        serializerContext.append("[")
        serializerContext.serializeAsExpression(constantPartSelectExpression.msbIndex)
        serializerContext.append(":")
        serializerContext.serializeAsExpression(constantPartSelectExpression.lsbIndex)
        serializerContext.append("]")
    }

    fun serializeConcatenationExpression(
        concatenationExpression: EConcatenationExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("{ ")
        serializerContext.join(concatenationExpression.expressions) {
            serializerContext.serializeAsExpression(it)
        }
        serializerContext.append(" }")
    }

    fun serializeReplicationExpression(
        replicationExpression: EReplicationExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("{")
        serializerContext.append(replicationExpression.value.toString())
        serializerContext.append("{ ")
        serializerContext.serializeAsExpression(replicationExpression.expression)
        serializerContext.append(" }}")
    }

    fun serializeStreamingExpression(
        streamingExpression: EStreamingExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("{<<{ ")
        serializerContext.serializeAsExpression(streamingExpression.expression)
        serializerContext.append(" }}")
    }

    fun serializeWidthCastExpression(
        widthCastExpression: EWidthCastExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("${widthCastExpression.value}'(")
        serializerContext.serializeAsExpression(widthCastExpression.expression)
        serializerContext.append(")")
    }

    fun serializeIfExpression(ifExpression: EIfExpression, serializerContext: SerializerContext) {
        serializerContext.append("if (")
        serializerContext.serializeAsExpression(ifExpression.condition)
        serializerContext.append(")")
        val thenExpression = ifExpression.thenExpression
        if (thenExpression != null) {
            if (thenExpression is ESvBlockExpression) {
                serializerContext.append(" ")
                serializerContext.serializeAsStatement(thenExpression)
            } else {
                serializerContext.appendLine()
                serializerContext.indent {
                    serializerContext.serializeAsStatement(thenExpression)
                }
            }
        } else {
            serializerContext.appendLine(";")
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            if (elseExpression is ESvBlockExpression || elseExpression is EIfExpression) {
                serializerContext.append("else ")
                serializerContext.serializeAsStatement(elseExpression)
            } else {
                serializerContext.appendLine("else")
                serializerContext.indent {
                    serializerContext.serializeAsStatement(elseExpression)
                }
            }
        }
    }

    fun serializeInlineIfExpression(inlineIfExpression: EInlineIfExpression, serializerContext: SerializerContext) {
        serializerContext.serializeAsExpression(inlineIfExpression.condition)
        serializerContext.hardBreak()
        serializerContext.append("? ")
        serializerContext.serializeAsExpression(inlineIfExpression.thenExpression)
        serializerContext.hardBreak()
        serializerContext.append(": ")
        serializerContext.serializeAsExpression(inlineIfExpression.elseExpression)
    }

    fun serializeCaseStatement(caseStatement: ECaseStatement, serializerContext: SerializerContext) {
        serializerContext.append("case (")
        serializerContext.serializeAsExpression(caseStatement.subject)
        serializerContext.appendLine(")")
        serializerContext.indent {
            caseStatement.entries.forEach { entry ->
                if (entry.conditions.isNotEmpty()) {
                    serializerContext.join(entry.conditions) {
                        serializerContext.serializeAsExpression(it)
                    }
                } else {
                    serializerContext.append("default")
                }
                serializerContext.append(" : ")
                serializerContext.serializeAsStatement(entry.body)
            }
        }
        serializerContext.appendLine("endcase")
    }

    fun serializeWhileExpression(whileExpression: EWhileExpression, serializerContext: SerializerContext) {
        if (whileExpression.isDoWhile) {
            serializerContext.append("do ")
            serializerContext.serializeAsStatement(whileExpression.body)
            serializerContext.append("while (")
            serializerContext.serializeAsExpression(whileExpression.condition)
            serializerContext.appendLine(");")
        } else {
            serializerContext.append("while (")
            serializerContext.serializeAsExpression(whileExpression.condition)
            serializerContext.append(") ")
            serializerContext.serializeAsStatement(whileExpression.body)
        }
    }

    fun serializeForStatement(forStatement: EForStatement, serializerContext: SerializerContext) {
        serializerContext.append("for (")
        serializerContext.serialize(forStatement.valueParameter)
        serializerContext.append(" = ")
        serializerContext.serializeAsExpression(forStatement.initializer)
        serializerContext.append("; ")
        serializerContext.serializeAsExpression(forStatement.condition)
        serializerContext.append("; ")
        serializerContext.serializeAsExpression(forStatement.iteration)
        serializerContext.append(") ")
        serializerContext.serializeAsStatement(forStatement.body)
    }

    fun serializeForeverStatement(foreverStatement: EForeverStatement, serializerContext: SerializerContext) {
        serializerContext.append("forever ")
        serializerContext.serializeAsStatement(foreverStatement.body)
    }

    fun serializeRepeatStatement(repeatStatement: ERepeatStatement, serializerContext: SerializerContext) {
        serializerContext.append("repeat (")
        serializerContext.serializeAsExpression(repeatStatement.condition)
        serializerContext.append(") ")
        serializerContext.serializeAsStatement(repeatStatement.body)
    }

    fun serializeEventExpression(eventExpression: EEventExpression, serializerContext: SerializerContext) {
        when (eventExpression.edgeType) {
            EdgeType.POSEDGE -> serializerContext.append("posedge ")
            EdgeType.NEGEDGE -> serializerContext.append("negedge ")
            EdgeType.EDGE -> serializerContext.append("edge ")
        }
        serializerContext.serializeAsExpression(eventExpression.expression)
    }

    fun serializeEventControlExpression(
        eventControlExpression: EEventControlExpression,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("@")
        serializerContext.serializeAsExpression(eventControlExpression.expression)
    }

    fun serializeDelayExpression(delayExpression: EDelayExpression, serializerContext: SerializerContext) {
        serializerContext.append("#")
        serializerContext.serializeAsExpression(delayExpression.expression)
    }
}
