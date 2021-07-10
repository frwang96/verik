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

import io.verik.compiler.ast.element.common.VkDeclaration
import io.verik.compiler.ast.element.common.VkElement
import io.verik.compiler.ast.element.common.VkFile
import io.verik.compiler.ast.element.sv.VkModule
import io.verik.compiler.ast.element.sv.VkSvClass
import io.verik.compiler.ast.element.sv.VkSvFunction
import io.verik.compiler.ast.element.sv.VkSvProperty
import io.verik.compiler.common.Visitor
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

    override fun visitSvClass(svClass: VkSvClass) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("class $svClass;", svClass)
        sourceBuilder.indent {
            svClass.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass: $svClass", svClass)
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

    override fun visitSvFunction(svFunction: VkSvFunction) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(svFunction)
        sourceBuilder.appendLine("function $typeString $svFunction();", svFunction)
        val bodyBlockExpression = svFunction.bodyBlockExpression
        if (bodyBlockExpression != null) {
            sourceBuilder.indent {
                bodyBlockExpression.accept(expressionVisitor)
            }
        }
        sourceBuilder.appendLine("endfunction: $svFunction", svFunction)
    }

    override fun visitSvProperty(svProperty: VkSvProperty) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(svProperty)
        sourceBuilder.append("$typeString $svProperty", svProperty)
        val initializer = svProperty.initializer
        if (initializer != null) {
            sourceBuilder.append(" = ", svProperty)
            expressionVisitor.serializeAsExpression(initializer)
            sourceBuilder.appendLine(";", initializer)
        } else {
            sourceBuilder.appendLine(";", svProperty)
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