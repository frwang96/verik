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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ESvAbstractFunction
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EParenthesizedExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.EWhileStatement
import io.verik.compiler.ast.element.expression.sv.EArrayLiteralExpression
import io.verik.compiler.ast.element.expression.sv.ECaseStatement
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.element.expression.sv.EEventExpression
import io.verik.compiler.ast.element.expression.sv.EForeverStatement
import io.verik.compiler.ast.element.expression.sv.EForkStatement
import io.verik.compiler.ast.element.expression.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
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
import io.verik.compiler.ast.element.expression.sv.EWidthCastExpression
import io.verik.compiler.ast.property.EdgeKind
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry

object ExpressionSerializer {

    fun serializeBlockExpression(blockExpression: EBlockExpression, serializeContext: SerializeContext) {
        serializeContext.appendLine("begin")
        serializeContext.indent {
            blockExpression.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(blockExpression.endLocation) {
            serializeContext.appendLine("end")
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

    fun serializeUnaryExpression(unaryExpression: ESvUnaryExpression, serializeContext: SerializeContext) {
        val prefix = unaryExpression.kind.serializePrefix()
        if (prefix != null)
            serializeContext.append(prefix)
        serializeContext.serializeAsExpression(unaryExpression.expression)
        val postfix = unaryExpression.kind.serializePostfix()
        if (postfix != null)
            serializeContext.append(postfix)
    }

    fun serializeBinaryExpression(binaryExpression: ESvBinaryExpression, serializeContext: SerializeContext) {
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

    fun serializeCallExpression(callExpression: ECallExpression, serializeContext: SerializeContext) {
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
        serializedType.checkNoVariableDimension(scopeExpression)
        serializeContext.append(serializedType.base)
        if (scopeExpression.typeParameters.isNotEmpty()) {
            val typeArgumentString = scopeExpression.typeParameters.joinToString {
                val typeArgumentSerializedType = TypeSerializer.serialize(it.type, scopeExpression)
                typeArgumentSerializedType.checkNoVariableDimension(scopeExpression)
                if (typeArgumentSerializedType.isVirtual) {
                    ".${it.name}(virtual ${typeArgumentSerializedType.base})"
                } else {
                    ".${it.name}(${typeArgumentSerializedType.base})"
                }
            }
            serializeContext.append(" #($typeArgumentString)")
        }
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

    fun serializeArrayLiteralExpression(
        arrayLiteralExpression: EArrayLiteralExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("'{default:${arrayLiteralExpression.default}}")
    }

    fun serializeThisExpression(serializeContext: SerializeContext) {
        serializeContext.append("this")
    }

    fun serializeSuperExpression(serializeContext: SerializeContext) {
        serializeContext.append("super")
    }

    fun serializeBreakStatement(serializeContext: SerializeContext) {
        serializeContext.appendLine("break;")
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

    fun serializeInjectedExpression(injectedStatement: EInjectedExpression, serializeContext: SerializeContext) {
        injectedStatement.entries.forEach {
            when (it) {
                is LiteralStringEntry -> {
                    if (it.text == "\n") serializeContext.appendLine()
                    else serializeContext.append(it.text)
                }
                is ExpressionStringEntry -> {
                    serializeContext.serializeAsExpression(it.expression)
                }
            }
        }
    }

    fun serializeStringExpression(stringExpression: EStringExpression, serializeContext: SerializeContext) {
        serializeContext.append("\"${stringExpression.text}\"")
    }

    fun serializeArrayAccessExpression(
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
        serializeContext.serializeAsExpression(constantPartSelectExpression.startIndex)
        serializeContext.append(":")
        serializeContext.serializeAsExpression(constantPartSelectExpression.endIndex)
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
        when (eventExpression.kind) {
            EdgeKind.POSEDGE -> serializeContext.append("posedge ")
            EdgeKind.NEGEDGE -> serializeContext.append("negedge ")
            EdgeKind.EDGE -> serializeContext.append("edge ")
        }
        serializeContext.serializeAsExpression(eventExpression.expression)
    }

    fun serializeEventControlExpression(
        eventControlExpression: EEventControlExpression,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("@(")
        serializeContext.serializeJoin(eventControlExpression.expressions) {
            serializeContext.serializeAsExpression(it)
        }
        serializeContext.append(")")
    }

    private fun serializePropertyInline(property: EProperty, serializeContext: SerializeContext) {
        val serializedType = TypeSerializer.serialize(property.type, property)
        serializedType.checkNoVariableDimension(property)
        serializeContext.append("${serializedType.base} ")
        serializeContext.append(property.name)
        val initializer = property.initializer
        if (initializer != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(initializer)
        }
    }
}
