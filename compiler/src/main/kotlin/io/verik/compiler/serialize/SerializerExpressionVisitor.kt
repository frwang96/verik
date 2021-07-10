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
import io.verik.compiler.ast.element.sv.SBinaryExpression
import io.verik.compiler.ast.element.sv.SForeverExpression
import io.verik.compiler.ast.element.sv.SStringExpression
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class SerializerExpressionVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    fun serializeAsExpression(element: CElement) {
        element.accept(this)
        if (!isSvExpression(element))
            m.error("SystemVerilog expression expected but got: ${element::class.simpleName}", element)
    }

    private fun serializeAsStatement(element: CElement) {
        element.accept(this)
        if (isSvExpression(element))
            sourceBuilder.appendLine(";", element)
    }

    private fun isSvExpression(element: CElement): Boolean {
        return when (element) {
            is CParenthesizedExpression -> true
            is SBinaryExpression -> true
            is CReferenceExpression -> true
            is CCallExpression -> true
            is CDotQualifiedExpression -> true
            is CConstantExpression -> true
            is SStringExpression -> true
            else -> false
        }
    }

    override fun visitCElement(element: CElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitCBlockExpression(blockExpression: CBlockExpression) {
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

    override fun visitCParenthesizedExpression(parenthesizedExpression: CParenthesizedExpression) {
        sourceBuilder.append("(", parenthesizedExpression)
        serializeAsExpression(parenthesizedExpression.expression)
        sourceBuilder.append(")", parenthesizedExpression)
    }

    override fun visitSBinaryExpression(binaryExpression: SBinaryExpression) {
        serializeAsExpression(binaryExpression.left)
        sourceBuilder.hardBreak()
        sourceBuilder.append(binaryExpression.kind.serialize(), binaryExpression)
        sourceBuilder.append(" ", binaryExpression)
        serializeAsExpression(binaryExpression.right)
    }

    override fun visitCReferenceExpression(referenceExpression: CReferenceExpression) {
        sourceBuilder.append(referenceExpression.reference.name.toString(), referenceExpression)
    }

    override fun visitCCallExpression(callExpression: CCallExpression) {
        sourceBuilder.append(callExpression.reference.name.toString(), callExpression)
        if (callExpression.valueArguments.isEmpty()) {
            sourceBuilder.append("()", callExpression)
        } else {
            sourceBuilder.append("(", callExpression)
            sourceBuilder.softBreak()
            visitCValueArgument(callExpression.valueArguments[0])
            callExpression.valueArguments.drop(1).forEach {
                sourceBuilder.append(",", callExpression)
                sourceBuilder.hardBreak()
                visitCValueArgument(it)
            }
            sourceBuilder.append(")", callExpression)
        }
    }

    override fun visitCValueArgument(valueArgument: CValueArgument) {
        serializeAsExpression(valueArgument.expression)
    }

    override fun visitCDotQualifiedExpression(dotQualifiedExpression: CDotQualifiedExpression) {
        serializeAsExpression(dotQualifiedExpression.receiver)
        sourceBuilder.append(".", dotQualifiedExpression)
        serializeAsExpression(dotQualifiedExpression.selector)
    }

    override fun visitCConstantExpression(constantExpression: CConstantExpression) {
        sourceBuilder.append(constantExpression.value, constantExpression)
    }

    override fun visitSStringExpression(stringExpression: SStringExpression) {
        sourceBuilder.append("\"${stringExpression.text}\"", stringExpression)
    }

    override fun visitSForeverExpression(foreverExpression: SForeverExpression) {
        sourceBuilder.append("forever ", foreverExpression)
        serializeAsStatement(foreverExpression.bodyBlockExpression)
    }
}