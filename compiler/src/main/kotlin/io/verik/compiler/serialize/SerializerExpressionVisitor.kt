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

import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.ast.element.*
import io.verik.compiler.main.m

class SerializerExpressionVisitor(private val sourceBuilder: SourceBuilder): Visitor() {

    override fun visitElement(element: VkElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitBlockExpression(blockExpression: VkBlockExpression) {
        blockExpression.statements.forEach {
            it.accept(this)
            sourceBuilder.appendLine(";", it)
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: VkParenthesizedExpression) {
        sourceBuilder.append("(", parenthesizedExpression)
        parenthesizedExpression.expression.accept(this)
        sourceBuilder.append(")", parenthesizedExpression)
    }

    override fun visitBinaryExpression(binaryExpression: VkBinaryExpression) {
        binaryExpression.left.accept(this)
        sourceBuilder.hardBreak()
        sourceBuilder.append(binaryExpression.kind.serialize(), binaryExpression)
        sourceBuilder.append(" ", binaryExpression)
        binaryExpression.right.accept(this)
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
            callExpression.valueArguments[0].accept(this)
            callExpression.valueArguments.drop(1).forEach {
                sourceBuilder.append(",", callExpression)
                sourceBuilder.hardBreak()
                it.accept(this)
            }
            sourceBuilder.append(")", callExpression)
        }
    }

    override fun visitValueArgument(valueArgument: VkValueArgument) {
        valueArgument.expression.accept(this)
    }

    override fun visitConstantExpression(constantExpression: VkConstantExpression) {
        sourceBuilder.append(constantExpression.value, constantExpression)
    }
}