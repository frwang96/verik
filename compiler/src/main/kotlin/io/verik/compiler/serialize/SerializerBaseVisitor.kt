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

class SerializerBaseVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    private var first = true
    private val expressionVisitor = SerializerExpressionVisitor(sourceBuilder)

    override fun visitElement(element: VkElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitFile(file: VkFile) {
        file.declarations.forEach { it.accept(this) }
    }

    override fun visitDeclaration(declaration: VkDeclaration) {
        m.error("Unable to serialize declaration: $declaration", declaration)
    }

    override fun visitBaseClass(baseClass: VkBaseClass) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("class $baseClass;", baseClass)
        sourceBuilder.indent {
            baseClass.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass: $baseClass", baseClass)
    }

    override fun visitModule(module: VkModule) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("module $module;", module)
        sourceBuilder.indent {
            module.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule: $module", module)
    }

    override fun visitBaseFunction(baseFunction: VkBaseFunction) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(baseFunction)
        sourceBuilder.appendLine("function $typeString $baseFunction();", baseFunction)
        val bodyBlockExpression = baseFunction.bodyBlockExpression
        if (bodyBlockExpression != null) {
            sourceBuilder.indent {
                bodyBlockExpression.accept(expressionVisitor)
            }
        }
        sourceBuilder.appendLine("endfunction: $baseFunction", baseFunction)
    }

    override fun visitBaseProperty(baseProperty: VkBaseProperty) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(baseProperty)
        sourceBuilder.append("$typeString $baseProperty", baseProperty)
        val initializer = baseProperty.initializer
        if (initializer != null) {
            sourceBuilder.append(" = ", baseProperty)
            initializer.accept(expressionVisitor)
            sourceBuilder.appendLine(";", initializer)
        } else {
            sourceBuilder.appendLine(";", baseProperty)
        }
    }

    private fun appendLineIfNotFirst() {
        if (first) {
            first = false
        } else {
            sourceBuilder.appendLine()
        }
    }
}