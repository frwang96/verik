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
import io.verik.compiler.ast.element.VkBlockExpression
import io.verik.compiler.ast.element.VkCallExpression
import io.verik.compiler.ast.element.VkConstantExpression
import io.verik.compiler.ast.element.VkReferenceExpression

class SerializerExpressionVisitor(private val sourceBuilder: SourceBuilder): Visitor() {

    override fun visitBlockExpression(blockExpression: VkBlockExpression) {
        blockExpression.statements.forEach {
            it.accept(this)
            sourceBuilder.appendLine(";", it)
        }
    }

    override fun visitReferenceExpression(referenceExpression: VkReferenceExpression) {
        sourceBuilder.append(referenceExpression.name.toString(), referenceExpression)
    }

    override fun visitCallExpression(callExpression: VkCallExpression) {
        sourceBuilder.append("${callExpression.name}(", callExpression)
        sourceBuilder.softBreak()
        sourceBuilder.append(")", callExpression)
    }

    override fun visitConstantExpression(constantExpression: VkConstantExpression) {
        sourceBuilder.append(constantExpression.value, constantExpression)
    }
}