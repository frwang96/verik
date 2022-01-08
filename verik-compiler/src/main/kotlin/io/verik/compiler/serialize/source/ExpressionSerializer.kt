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
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EForkStatement
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.EInjectedStatement
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
import io.verik.compiler.ast.element.sv.ESvForStatement
import io.verik.compiler.ast.element.sv.ESvProperty
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
            serializerContext.label(blockExpression.endLocation) {
                serializerContext.append("end")
                if (blockExpression.name != null)
                    serializerContext.append(" : ${blockExpression.name}")
                serializerContext.appendLine()
            }
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

    fun serializeReferenceExpression(
        referenceExpression: EReferenceExpression,
        serializerContext: SerializerContext
    ) {
        val receiver = referenceExpression.receiver
        if (receiver != null) {
            serializerContext.serializeAsExpression(receiver)
            serializerContext.append(if (receiver is EScopeExpression) "::" else ".")
        }
        serializerContext.append(referenceExpression.reference.name)
    }

    fun serializeSvCallExpression(callExpression: ESvCallExpression, serializerContext: SerializerContext) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            serializerContext.serializeAsExpression(receiver)
            serializerContext.append(if (receiver is EScopeExpression) "::" else ".")
        }
        serializerContext.append(callExpression.reference.name)
        serializerContext.append("(")
        if (callExpression.valueArguments.isNotEmpty()) {
            serializerContext.softBreak()
            serializerContext.serializeJoin(callExpression.valueArguments) {
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
        serializerContext.serializeJoin(structLiteralExpression.entries) {
            serializerContext.append("${it.reference.name}:")
            serializerContext.serializeAsExpression(it.expression)
        }
        serializerContext.append("}")
    }

    fun serializeNullExpression(serializerContext: SerializerContext) {
        serializerContext.append("null")
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

    fun serializeInjectedStatement(injectedStatement: EInjectedStatement, serializerContext: SerializerContext) {
        injectedStatement.entries.forEach {
            when (it) {
                is LiteralStringEntry -> {
                    if (it.text == "\n")
                        serializerContext.appendLine()
                    else
                        serializerContext.append(it.text)
                }
                is ExpressionStringEntry ->
                    serializerContext.serializeAsExpression(it.expression)
            }
        }
        serializerContext.appendLine()
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
        serializerContext.serializeJoin(concatenationExpression.expressions) {
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
            serializerContext.append(" ")
            serializerContext.serializeAsStatement(thenExpression)
        } else {
            serializerContext.appendLine(";")
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            serializerContext.label(elseExpression) {
                serializerContext.append("else ")
                if (elseExpression.statements.size == 1 && elseExpression.statements[0] is EIfExpression) {
                    serializerContext.serializeAsStatement(elseExpression.statements[0])
                } else {
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

    fun serializeImmediateAssertStatement(
        immediateAssertStatement: EImmediateAssertStatement,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("assert (")
        serializerContext.serializeAsExpression(immediateAssertStatement.condition)
        serializerContext.append(")")
        val elseExpression = immediateAssertStatement.elseExpression
        if (elseExpression != null) {
            serializerContext.append(" else ")
            serializerContext.serializeAsStatement(elseExpression)
        } else {
            serializerContext.appendLine(";")
        }
    }

    fun serializeCaseStatement(caseStatement: ECaseStatement, serializerContext: SerializerContext) {
        serializerContext.append("case (")
        serializerContext.serializeAsExpression(caseStatement.subject)
        serializerContext.appendLine(")")
        serializerContext.indent {
            caseStatement.entries.forEach { entry ->
                if (entry.conditions.isNotEmpty()) {
                    serializerContext.serializeJoin(entry.conditions) {
                        serializerContext.serializeAsExpression(it)
                    }
                } else {
                    serializerContext.append("default")
                }
                serializerContext.append(" : ")
                serializerContext.serializeAsStatement(entry.body)
            }
        }
        serializerContext.label(caseStatement.endLocation) {
            serializerContext.appendLine("endcase")
        }
    }

    fun serializeWhileStatement(whileStatement: EWhileStatement, serializerContext: SerializerContext) {
        if (whileStatement.isDoWhile) {
            serializerContext.append("do ")
            serializerContext.serializeAsStatement(whileStatement.body)
            serializerContext.append("while (")
            serializerContext.serializeAsExpression(whileStatement.condition)
            serializerContext.appendLine(");")
        } else {
            serializerContext.append("while (")
            serializerContext.serializeAsExpression(whileStatement.condition)
            serializerContext.append(") ")
            serializerContext.serializeAsStatement(whileStatement.body)
        }
    }

    fun serializeForStatement(forStatement: ESvForStatement, serializerContext: SerializerContext) {
        serializerContext.append("for (")
        serializePropertyInline(forStatement.property, serializerContext)
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

    fun serializeForkStatement(forkStatement: EForkStatement, serializerContext: SerializerContext) {
        serializerContext.appendLine("fork")
        serializerContext.indent {
            serializerContext.serializeAsStatement(forkStatement.body)
        }
        serializerContext.appendLine("join_none")
    }

    fun serializeWaitForkStatement(serializerContext: SerializerContext) {
        serializerContext.appendLine("wait fork;")
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
        serializerContext.append("@(")
        serializerContext.serializeAsExpression(eventControlExpression.expression)
        serializerContext.append(")")
    }

    fun serializeDelayExpression(delayExpression: EDelayExpression, serializerContext: SerializerContext) {
        serializerContext.append("#")
        serializerContext.serializeAsExpression(delayExpression.expression)
    }

    private fun serializePropertyInline(property: ESvProperty, serializerContext: SerializerContext) {
        val serializedType = TypeSerializer.serialize(property.type, property)
        serializedType.checkNoUnpackedDimension(property)
        serializerContext.append(serializedType.getBaseAndPackedDimension() + " ")
        serializerContext.append(property.name)
        val initializer = property.initializer
        if (initializer != null) {
            serializerContext.append(" = ")
            serializerContext.serializeAsExpression(initializer)
        }
    }
}
