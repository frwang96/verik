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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class BaseSerializerVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    private var first = true
    private val expressionSerializerVisitor = ExpressionSerializerVisitor(sourceBuilder)

    override fun visitElement(element: EElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitFile(file: EFile) {
        file.declarations.forEach { it.accept(this) }
    }

    override fun visitDeclaration(declaration: EDeclaration) {
        m.error("Unable to serialize declaration $declaration: Found ${declaration::class.simpleName}", declaration)
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("class $basicClass;", basicClass)
        sourceBuilder.indent {
            basicClass.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass : $basicClass", basicClass)
    }

    override fun visitModule(module: EModule) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("module $module;", module)
        sourceBuilder.indent {
            module.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule : $module", module)
    }

    override fun visitSvFunction(function: ESvFunction) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(function)
        sourceBuilder.appendLine("function $typeString $function();", function)
        val body = function.body
        if (body != null) {
            sourceBuilder.indent {
                expressionSerializerVisitor.serializeAsStatement(body)
            }
        }
        sourceBuilder.appendLine("endfunction : $function", function)
    }

    override fun visitSvProperty(property: ESvProperty) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(property)
        sourceBuilder.append("$typeString $property", property)
        val initializer = property.initializer
        if (initializer != null) {
            sourceBuilder.append(" = ", property)
            expressionSerializerVisitor.serializeAsExpression(initializer)
            sourceBuilder.appendLine(";", initializer)
        } else {
            sourceBuilder.appendLine(";", property)
        }
    }

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        appendLineIfNotFirst()
        sourceBuilder.append("initial ", initialBlock)
        expressionSerializerVisitor.serializeAsStatement(initialBlock.body)
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        appendLineIfNotFirst()
        sourceBuilder.append("always_comb ", alwaysComBlock)
        expressionSerializerVisitor.serializeAsStatement(alwaysComBlock.body)
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        appendLineIfNotFirst()
        sourceBuilder.append("always_ff ", alwaysSeqBlock)
        expressionSerializerVisitor.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        sourceBuilder.append(" ", alwaysSeqBlock)
        expressionSerializerVisitor.serializeAsStatement(alwaysSeqBlock.body)
    }

    private fun appendLineIfNotFirst() {
        if (first) {
            first = false
        } else {
            sourceBuilder.appendLine()
        }
    }
}