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
import io.verik.compiler.ast.element.sv.VkStringExpression
import io.verik.compiler.ast.element.sv.VkSvBinaryExpression
import io.verik.compiler.ast.element.sv.VkSvForeverExpression
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class SerializerExpressionVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    fun serializeAsExpression(element: VkElement) {
        element.accept(this)
        if (!isSvExpression(element))
            m.error("SystemVerilog expression expected but got: ${element::class.simpleName}", element)
    }

    private fun serializeAsStatement(element: VkElement) {
        element.accept(this)
        if (isSvExpression(element))
            sourceBuilder.appendLine(";", element)
    }

    private fun isSvExpression(element: VkElement): Boolean {
        return when (element) {
            is VkParenthesizedExpression -> true
            is VkSvBinaryExpression -> true
            is VkReferenceExpression -> true
            is VkCallExpression -> true
            is VkDotQualifiedExpression -> true
            is VkConstantExpression -> true
            is VkStringExpression -> true
            else -> false
        }
    }

    override fun visitElement(element: VkElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitBlockExpression(blockExpression: VkBlockExpression) {
        if (blockExpression.decorated) {
            sourceBuilder.appendLine("begin", blockExpression)
            sourceBuilder.indent {
                blockExpression.statements.forEach { serializeAsStatement(it) }
            }
            sourceBuilder.appendLine("end", blockExpression)
        } else {
            blockExpression.statements.forEach { serializeAsStatement(it) }
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: VkParenthesizedExpression) {
        sourceBuilder.append("(", parenthesizedExpression)
        serializeAsExpression(parenthesizedExpression.expression)
        sourceBuilder.append(")", parenthesizedExpression)
    }

    override fun visitSvBinaryExpression(svBinaryExpression: VkSvBinaryExpression) {
        serializeAsExpression(svBinaryExpression.left)
        sourceBuilder.hardBreak()
        sourceBuilder.append(svBinaryExpression.kind.serialize(), svBinaryExpression)
        sourceBuilder.append(" ", svBinaryExpression)
        serializeAsExpression(svBinaryExpression.right)
    }

    override fun visitReferenceExpression(referenceExpression: VkReferenceExpression) {
        sourceBuilder.append(referenceExpression.reference.name.toString(), referenceExpression)
    }

    override fun visitCallExpression(callExpression: VkCallExpression) {
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

    override fun visitValueArgument(valueArgument: VkValueArgument) {
        serializeAsExpression(valueArgument.expression)
    }

    override fun visitDotQualifiedExpression(dotQualifiedExpression: VkDotQualifiedExpression) {
        serializeAsExpression(dotQualifiedExpression.receiver)
        sourceBuilder.append(".", dotQualifiedExpression)
        serializeAsExpression(dotQualifiedExpression.selector)
    }

    override fun visitConstantExpression(constantExpression: VkConstantExpression) {
        sourceBuilder.append(constantExpression.value, constantExpression)
    }

    override fun visitStringExpression(stringExpression: VkStringExpression) {
        sourceBuilder.append("\"${stringExpression.text}\"", stringExpression)
    }

    override fun visitSvForeverExpression(svForeverExpression: VkSvForeverExpression) {
        sourceBuilder.append("forever ", svForeverExpression)
        serializeAsStatement(svForeverExpression.bodyBlockExpression)
    }
}