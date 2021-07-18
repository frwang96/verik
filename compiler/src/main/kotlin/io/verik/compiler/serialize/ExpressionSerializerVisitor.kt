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

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class ExpressionSerializerVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    fun serializeAsExpression(expression: EExpression) {
        expression.accept(this)
        if (expression.serializationType != SvSerializationType.EXPRESSION)
            m.error("SystemVerilog expression expected but got: $expression", expression)
    }

    fun serializeAsStatement(expression: EExpression) {
        expression.accept(this)
        when (expression.serializationType) {
            SvSerializationType.EXPRESSION -> sourceBuilder.appendLine(";", expression)
            SvSerializationType.STATEMENT -> {}
            SvSerializationType.OTHER ->
                m.error("SystemVerilog expression or statement expected but got: $expression", expression)
        }
    }

    override fun visitElement(element: EElement) {
        m.error("Unable to serialize element: $element", element)
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        if (blockExpression.decorated) {
            sourceBuilder.append("begin", blockExpression)
            if (blockExpression.name != null)
                sourceBuilder.append(" : ${blockExpression.name}", blockExpression)
            sourceBuilder.appendLine()
            sourceBuilder.indent {
                blockExpression.statements.forEach { serializeAsStatement(it) }
            }
            sourceBuilder.append("end", blockExpression)
            if (blockExpression.name != null)
                sourceBuilder.append(" : ${blockExpression.name}", blockExpression)
            sourceBuilder.appendLine()
        } else {
            blockExpression.statements.forEach { serializeAsStatement(it) }
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        sourceBuilder.append("(", parenthesizedExpression)
        serializeAsExpression(parenthesizedExpression.expression)
        sourceBuilder.append(")", parenthesizedExpression)
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        sourceBuilder.append(unaryExpression.kind.serialize(), unaryExpression)
        serializeAsExpression(unaryExpression.expression)
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        serializeAsExpression(binaryExpression.left)
        sourceBuilder.hardBreak()
        sourceBuilder.append(binaryExpression.kind.serialize(), binaryExpression)
        sourceBuilder.append(" ", binaryExpression)
        serializeAsExpression(binaryExpression.right)
    }

    override fun visitSimpleNameExpression(simpleNameExpression: ESimpleNameExpression) {
        val receiver = simpleNameExpression.receiver
        if (receiver != null) {
            serializeAsExpression(receiver)
            sourceBuilder.append(".", simpleNameExpression)
        }
        sourceBuilder.append(simpleNameExpression.reference.name.toString(), simpleNameExpression)
    }

    override fun visitCallExpression(callExpression: ECallExpression) {
        val receiver = callExpression.receiver
        if (receiver != null) {
            serializeAsExpression(receiver)
            sourceBuilder.append(".", callExpression)
        }
        sourceBuilder.append(callExpression.reference.name.toString(), callExpression)
        if (callExpression.valueArguments.isEmpty()) {
            sourceBuilder.append("()", callExpression)
        } else {
            sourceBuilder.append("(", callExpression)
            sourceBuilder.softBreak()
            visitValueArgument(callExpression.valueArguments[0])
            callExpression.valueArguments.drop(1).forEach {
                sourceBuilder.append(",", callExpression)
                sourceBuilder.hardBreak()
                visitValueArgument(it)
            }
            sourceBuilder.append(")", callExpression)
        }
    }

    override fun visitValueArgument(valueArgument: EValueArgument) {
        serializeAsExpression(valueArgument.expression)
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        sourceBuilder.append(constantExpression.value, constantExpression)
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        sourceBuilder.append("\"${stringExpression.text}\"", stringExpression)
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        sourceBuilder.append("if (", ifExpression)
        serializeAsExpression(ifExpression.condition)
        sourceBuilder.append(")", ifExpression)
        val thenExpression = ifExpression.thenExpression
        if (thenExpression != null) {
            if (thenExpression is ESvBlockExpression) {
                sourceBuilder.append(" ", ifExpression)
                serializeAsStatement(thenExpression)
            }
            else {
                sourceBuilder.appendLine()
                sourceBuilder.indent {
                    serializeAsStatement(thenExpression)
                }
            }
        } else {
            sourceBuilder.appendLine(";", ifExpression)
        }
        val elseExpression = ifExpression.elseExpression
        if (elseExpression != null) {
            if (elseExpression is ESvBlockExpression || elseExpression is EIfExpression) {
                sourceBuilder.append("else ", ifExpression)
                serializeAsStatement(elseExpression)
            } else {
                sourceBuilder.appendLine("else", ifExpression)
                sourceBuilder.indent {
                    serializeAsStatement(elseExpression)
                }
            }
        }
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        sourceBuilder.append("forever ", foreverStatement)
        serializeAsStatement(foreverStatement.body)
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        when (eventExpression.edgeType) {
            EdgeType.POSEDGE -> sourceBuilder.append("posedge ", eventExpression)
            EdgeType.NEGEDGE -> sourceBuilder.append("negedge ", eventExpression)
            EdgeType.EDGE -> sourceBuilder.append("edge ", eventExpression)
        }
        serializeAsExpression(eventExpression.expression)
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        sourceBuilder.append("@", eventControlExpression)
        serializeAsExpression(eventControlExpression.expression)
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        sourceBuilder.append("#", delayExpression)
        serializeAsExpression(delayExpression.expression)
    }
}