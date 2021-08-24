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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
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
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m
import io.verik.compiler.message.Messages

class ExpressionSerializerVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    fun serializeAsExpression(expression: EExpression) {
        sourceBuilder.label(expression) {
            if (expression.serializationType != SvSerializationType.EXPRESSION)
                m.error("SystemVerilog expression expected but got: $expression", expression)
            expression.accept(this)
        }
    }

    fun serializeAsStatement(expression: EExpression) {
        sourceBuilder.label(expression) {
            if (expression.serializationType !in listOf(SvSerializationType.EXPRESSION, SvSerializationType.STATEMENT))
                m.error("SystemVerilog expression or statement expected but got: $expression", expression)
            expression.accept(this)
            if (expression.serializationType == SvSerializationType.EXPRESSION)
                sourceBuilder.appendLine(";")
        }
    }

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element as expression: $element")
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        if (blockExpression.decorated) {
            sourceBuilder.append("begin")
            if (blockExpression.name != null)
                sourceBuilder.append(" : ${blockExpression.name}")
            sourceBuilder.appendLine()
            sourceBuilder.indent {
                blockExpression.statements.forEach { serializeAsStatement(it) }
            }
            sourceBuilder.append("end")
            if (blockExpression.name != null)
                sourceBuilder.append(" : ${blockExpression.name}")
            sourceBuilder.appendLine()
        } else {
            blockExpression.statements.forEach { serializeAsStatement(it) }
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        sourceBuilder.append("(")
        serializeAsExpression(parenthesizedExpression.expression)
        sourceBuilder.append(")")
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        sourceBuilder.append(unaryExpression.kind.serialize())
        serializeAsExpression(unaryExpression.expression)
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        serializeAsExpression(binaryExpression.left)
        sourceBuilder.hardBreak()
        sourceBuilder.append(binaryExpression.kind.serialize())
        sourceBuilder.append(" ")
        serializeAsExpression(binaryExpression.right)
    }

    override fun visitSvReferenceExpression(referenceExpression: ESvReferenceExpression) {
        val receiver = referenceExpression.receiver
        if (receiver != null) {
            serializeAsExpression(receiver)
            sourceBuilder.append(if (referenceExpression.isScopeResolution) "::" else ".")
        }
        sourceBuilder.append(referenceExpression.reference.name)
    }

    override fun visitSvCallExpression(callExpression: ESvCallExpression) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            serializeAsExpression(receiver)
            sourceBuilder.append(if (callExpression.isScopeResolution) "::" else ".")
        }
        sourceBuilder.append(callExpression.reference.name)
        if (callExpression.valueArguments.isEmpty()) {
            sourceBuilder.append("()")
        } else {
            sourceBuilder.append("(")
            sourceBuilder.softBreak()
            serializeAsExpression(callExpression.valueArguments[0])
            callExpression.valueArguments.drop(1).forEach {
                sourceBuilder.append(",")
                sourceBuilder.hardBreak()
                serializeAsExpression(it)
            }
            sourceBuilder.append(")")
        }
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        sourceBuilder.append(constantExpression.value)
    }

    override fun visitInjectedExpression(injectedExpression: EInjectedExpression) {
        injectedExpression.entries.forEach {
            when (it) {
                is LiteralStringEntry ->
                    sourceBuilder.append(it.text)
                is ExpressionStringEntry ->
                    serializeAsExpression(it.expression)
            }
        }
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        sourceBuilder.append("\"${stringExpression.text}\"")
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        sourceBuilder.append("if (")
        serializeAsExpression(ifExpression.condition)
        sourceBuilder.append(")")
        val thenExpression = ifExpression.thenExpression
        if (thenExpression != null) {
            if (thenExpression is ESvBlockExpression) {
                sourceBuilder.append(" ")
                serializeAsStatement(thenExpression)
            } else {
                sourceBuilder.appendLine()
                sourceBuilder.indent {
                    serializeAsStatement(thenExpression)
                }
            }
        } else {
            sourceBuilder.appendLine(";")
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            if (elseExpression is ESvBlockExpression || elseExpression is EIfExpression) {
                sourceBuilder.append("else ")
                serializeAsStatement(elseExpression)
            } else {
                sourceBuilder.appendLine("else")
                sourceBuilder.indent {
                    serializeAsStatement(elseExpression)
                }
            }
        }
    }

    override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        serializeAsExpression(inlineIfExpression.condition)
        sourceBuilder.hardBreak()
        sourceBuilder.append("? ")
        serializeAsExpression(inlineIfExpression.thenExpression)
        sourceBuilder.hardBreak()
        sourceBuilder.append(": ")
        serializeAsExpression(inlineIfExpression.elseExpression)
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        sourceBuilder.append("forever ")
        serializeAsStatement(foreverStatement.body)
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        when (eventExpression.edgeType) {
            EdgeType.POSEDGE -> sourceBuilder.append("posedge ")
            EdgeType.NEGEDGE -> sourceBuilder.append("negedge ")
            EdgeType.EDGE -> sourceBuilder.append("edge ")
        }
        serializeAsExpression(eventExpression.expression)
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        sourceBuilder.append("@")
        serializeAsExpression(eventControlExpression.expression)
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        sourceBuilder.append("#")
        serializeAsExpression(delayExpression.expression)
    }
}
