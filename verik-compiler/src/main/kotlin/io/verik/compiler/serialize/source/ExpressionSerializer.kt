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
import io.verik.compiler.ast.element.sv.ESvAbstractFunction
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

    fun serializeSvBlockExpression(blockExpression: ESvBlockExpression, serializeContext: SerializeContext) {
        if (blockExpression.decorated) {
            serializeContext.append("begin")
            if (blockExpression.name != null)
                serializeContext.append(" : ${blockExpression.name}")
            serializeContext.appendLine()
            serializeContext.indent {
                blockExpression.statements.forEach { serializeContext.serializeAsStatement(it) }
            }
            serializeContext.label(blockExpression.endLocation) {
                serializeContext.append("end")
                if (blockExpression.name != null)
                    serializeContext.append(" : ${blockExpression.name}")
                serializeContext.appendLine()
            }
        } else {
            blockExpression.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
    }

    fun serializePropertyStatement(propertyStatement: EPropertyStatement, serializeContext: SerializeContext) {
        serializeContext.serialize(propertyStatement.property)
    }

    fun serializeParenthesizedExpression(
        parenthesizedExpression: EParenthesizedExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("(")
        serializeContext.serializeAsExpression(parenthesizedExpression.expression)
        serializeContext.append(")")
    }

    fun serializeSvUnaryExpression(unaryExpression: ESvUnaryExpression, serializeContext: SerializeContext) {
        val prefix = unaryExpression.kind.serializePrefix()
        if (prefix != null)
            serializeContext.append(prefix)
        serializeContext.serializeAsExpression(unaryExpression.expression)
        val postfix = unaryExpression.kind.serializePostfix()
        if (postfix != null)
            serializeContext.append(postfix)
    }

    fun serializeSvBinaryExpression(binaryExpression: ESvBinaryExpression, serializeContext: SerializeContext) {
        serializeContext.serializeAsExpression(binaryExpression.left)
        serializeContext.hardBreak()
        serializeContext.append(binaryExpression.kind.serialize())
        serializeContext.append(" ")
        serializeContext.serializeAsExpression(binaryExpression.right)
    }

    fun serializeReferenceExpression(
        referenceExpression: EReferenceExpression,
        serializeContext: SerializeContext
    ) {
        val receiver = referenceExpression.receiver
        if (receiver != null) {
            serializeContext.serializeAsExpression(receiver)
            serializeContext.append(if (receiver is EScopeExpression) "::" else ".")
        }
        serializeContext.append(referenceExpression.reference.name)
    }

    fun serializeSvCallExpression(callExpression: ESvCallExpression, serializeContext: SerializeContext) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            serializeContext.serializeAsExpression(receiver)
            serializeContext.softBreak()
            serializeContext.append(if (receiver is EScopeExpression) "::" else ".")
        }
        serializeContext.append(callExpression.reference.name)
        serializeContext.append("(")
        if (callExpression.valueArguments.isNotEmpty()) {
            serializeContext.softBreak()
            val reference = callExpression.reference
            if (reference is ESvAbstractFunction) {
                val valueParametersAndArguments = reference.valueParameters.zip(callExpression.valueArguments)
                serializeContext.serializeJoin(valueParametersAndArguments) { (valueParameter, valueArgument) ->
                    serializeContext.append(".${valueParameter.name}(")
                    serializeContext.serializeAsExpression(valueArgument)
                    serializeContext.append(")")
                }
            } else {
                serializeContext.serializeJoin(callExpression.valueArguments) {
                    serializeContext.serializeAsExpression(it)
                }
            }
        }
        serializeContext.append(")")
    }

    fun serializeScopeExpression(scopeExpression: EScopeExpression, serializeContext: SerializeContext) {
        val serializedType = TypeSerializer.serialize(scopeExpression.scope, scopeExpression)
        serializedType.checkNoPackedDimension(scopeExpression)
        serializedType.checkNoUnpackedDimension(scopeExpression)
        serializeContext.append(serializedType.base)
    }

    fun serializeConstantExpression(constantExpression: EConstantExpression, serializeContext: SerializeContext) {
        serializeContext.append(constantExpression.value)
    }

    fun serializeStructLiteralExpression(
        structLiteralExpression: EStructLiteralExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("'{")
        serializeContext.serializeJoin(structLiteralExpression.entries) {
            serializeContext.append("${it.reference.name}:")
            serializeContext.serializeAsExpression(it.expression)
        }
        serializeContext.append("}")
    }

    fun serializeNullExpression(serializeContext: SerializeContext) {
        serializeContext.append("null")
    }

    fun serializeThisExpression(serializeContext: SerializeContext) {
        serializeContext.append("this")
    }

    fun serializeSuperExpression(serializeContext: SerializeContext) {
        serializeContext.append("super")
    }

    fun serializeReturnStatement(returnStatement: EReturnStatement, serializeContext: SerializeContext) {
        val expression = returnStatement.expression
        if (expression == null) {
            serializeContext.appendLine("return;")
        } else {
            serializeContext.append("return ")
            serializeContext.serializeAsExpression(expression)
            serializeContext.appendLine(";")
        }
    }

    fun serializeInjectedStatement(injectedStatement: EInjectedStatement, serializeContext: SerializeContext) {
        injectedStatement.entries.forEach {
            when (it) {
                is LiteralStringEntry -> {
                    if (it.text == "\n")
                        serializeContext.appendLine()
                    else
                        serializeContext.append(it.text)
                }
                is ExpressionStringEntry ->
                    serializeContext.serializeAsExpression(it.expression)
            }
        }
        serializeContext.appendLine()
    }

    fun serializeStringExpression(stringExpression: EStringExpression, serializeContext: SerializeContext) {
        serializeContext.append("\"${stringExpression.text}\"")
    }

    fun serializeSvArrayAccessExpression(
        arrayAccessExpression: ESvArrayAccessExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.serializeAsExpression(arrayAccessExpression.array)
        serializeContext.append("[")
        serializeContext.serializeAsExpression(arrayAccessExpression.index)
        serializeContext.append("]")
    }

    fun serializeConstantPartSelectExpression(
        constantPartSelectExpression: EConstantPartSelectExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.serializeAsExpression(constantPartSelectExpression.array)
        serializeContext.append("[")
        serializeContext.serializeAsExpression(constantPartSelectExpression.msbIndex)
        serializeContext.append(":")
        serializeContext.serializeAsExpression(constantPartSelectExpression.lsbIndex)
        serializeContext.append("]")
    }

    fun serializeConcatenationExpression(
        concatenationExpression: EConcatenationExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("{ ")
        serializeContext.serializeJoin(concatenationExpression.expressions) {
            serializeContext.serializeAsExpression(it)
        }
        serializeContext.append(" }")
    }

    fun serializeReplicationExpression(
        replicationExpression: EReplicationExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("{")
        serializeContext.append(replicationExpression.value.toString())
        serializeContext.append("{ ")
        serializeContext.serializeAsExpression(replicationExpression.expression)
        serializeContext.append(" }}")
    }

    fun serializeStreamingExpression(
        streamingExpression: EStreamingExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("{<<{ ")
        serializeContext.serializeAsExpression(streamingExpression.expression)
        serializeContext.append(" }}")
    }

    fun serializeWidthCastExpression(
        widthCastExpression: EWidthCastExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("${widthCastExpression.value}'(")
        serializeContext.serializeAsExpression(widthCastExpression.expression)
        serializeContext.append(")")
    }

    fun serializeIfExpression(ifExpression: EIfExpression, serializeContext: SerializeContext) {
        serializeContext.append("if (")
        serializeContext.serializeAsExpression(ifExpression.condition)
        serializeContext.append(")")
        val thenExpression = ifExpression.thenExpression
        if (thenExpression != null) {
            serializeContext.append(" ")
            serializeContext.serializeAsStatement(thenExpression)
        } else {
            serializeContext.appendLine(";")
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            serializeContext.label(elseExpression) {
                serializeContext.append("else ")
                if (elseExpression.statements.size == 1 && elseExpression.statements[0] is EIfExpression) {
                    serializeContext.serializeAsStatement(elseExpression.statements[0])
                } else {
                    serializeContext.serializeAsStatement(elseExpression)
                }
            }
        }
    }

    fun serializeInlineIfExpression(inlineIfExpression: EInlineIfExpression, serializeContext: SerializeContext) {
        serializeContext.serializeAsExpression(inlineIfExpression.condition)
        serializeContext.hardBreak()
        serializeContext.append("? ")
        serializeContext.serializeAsExpression(inlineIfExpression.thenExpression)
        serializeContext.hardBreak()
        serializeContext.append(": ")
        serializeContext.serializeAsExpression(inlineIfExpression.elseExpression)
    }

    fun serializeImmediateAssertStatement(
        immediateAssertStatement: EImmediateAssertStatement,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("assert (")
        serializeContext.serializeAsExpression(immediateAssertStatement.condition)
        serializeContext.append(")")
        val elseExpression = immediateAssertStatement.elseExpression
        if (elseExpression != null) {
            serializeContext.append(" else ")
            serializeContext.serializeAsStatement(elseExpression)
        } else {
            serializeContext.appendLine(";")
        }
    }

    fun serializeCaseStatement(caseStatement: ECaseStatement, serializeContext: SerializeContext) {
        serializeContext.append("case (")
        serializeContext.serializeAsExpression(caseStatement.subject)
        serializeContext.appendLine(")")
        serializeContext.indent {
            caseStatement.entries.forEach { entry ->
                if (entry.conditions.isNotEmpty()) {
                    serializeContext.serializeJoin(entry.conditions) {
                        serializeContext.serializeAsExpression(it)
                    }
                } else {
                    serializeContext.append("default")
                }
                serializeContext.append(" : ")
                serializeContext.serializeAsStatement(entry.body)
            }
        }
        serializeContext.label(caseStatement.endLocation) {
            serializeContext.appendLine("endcase")
        }
    }

    fun serializeWhileStatement(whileStatement: EWhileStatement, serializeContext: SerializeContext) {
        if (whileStatement.isDoWhile) {
            serializeContext.append("do ")
            serializeContext.serializeAsStatement(whileStatement.body)
            serializeContext.append("while (")
            serializeContext.serializeAsExpression(whileStatement.condition)
            serializeContext.appendLine(");")
        } else {
            serializeContext.append("while (")
            serializeContext.serializeAsExpression(whileStatement.condition)
            serializeContext.append(") ")
            serializeContext.serializeAsStatement(whileStatement.body)
        }
    }

    fun serializeForStatement(forStatement: ESvForStatement, serializeContext: SerializeContext) {
        serializeContext.append("for (")
        serializePropertyInline(forStatement.property, serializeContext)
        serializeContext.append("; ")
        serializeContext.serializeAsExpression(forStatement.condition)
        serializeContext.append("; ")
        serializeContext.serializeAsExpression(forStatement.iteration)
        serializeContext.append(") ")
        serializeContext.serializeAsStatement(forStatement.body)
    }

    fun serializeForeverStatement(foreverStatement: EForeverStatement, serializeContext: SerializeContext) {
        serializeContext.append("forever ")
        serializeContext.serializeAsStatement(foreverStatement.body)
    }

    fun serializeRepeatStatement(repeatStatement: ERepeatStatement, serializeContext: SerializeContext) {
        serializeContext.append("repeat (")
        serializeContext.serializeAsExpression(repeatStatement.condition)
        serializeContext.append(") ")
        serializeContext.serializeAsStatement(repeatStatement.body)
    }

    fun serializeForkStatement(forkStatement: EForkStatement, serializeContext: SerializeContext) {
        serializeContext.appendLine("fork")
        serializeContext.indent {
            serializeContext.serializeAsStatement(forkStatement.body)
        }
        serializeContext.appendLine("join_none")
    }

    fun serializeWaitForkStatement(serializeContext: SerializeContext) {
        serializeContext.appendLine("wait fork;")
    }

    fun serializeEventExpression(eventExpression: EEventExpression, serializeContext: SerializeContext) {
        when (eventExpression.edgeType) {
            EdgeType.POSEDGE -> serializeContext.append("posedge ")
            EdgeType.NEGEDGE -> serializeContext.append("negedge ")
            EdgeType.EDGE -> serializeContext.append("edge ")
        }
        serializeContext.serializeAsExpression(eventExpression.expression)
    }

    fun serializeEventControlExpression(
        eventControlExpression: EEventControlExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("@(")
        serializeContext.serializeAsExpression(eventControlExpression.expression)
        serializeContext.append(")")
    }

    fun serializeDelayExpression(delayExpression: EDelayExpression, serializeContext: SerializeContext) {
        serializeContext.append("#")
        serializeContext.serializeAsExpression(delayExpression.expression)
    }

    private fun serializePropertyInline(property: ESvProperty, serializeContext: SerializeContext) {
        val serializedType = TypeSerializer.serialize(property.type, property)
        serializedType.checkNoUnpackedDimension(property)
        serializeContext.append(serializedType.getBaseAndPackedDimension() + " ")
        serializeContext.append(property.name)
        val initializer = property.initializer
        if (initializer != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(initializer)
        }
    }
}
