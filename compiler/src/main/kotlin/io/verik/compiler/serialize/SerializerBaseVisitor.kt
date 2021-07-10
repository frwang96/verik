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

import io.verik.compiler.ast.element.common.CDeclaration
import io.verik.compiler.ast.element.common.CElement
import io.verik.compiler.ast.element.common.CFile
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class SerializerBaseVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    private var first = true
    private val expressionVisitor = SerializerExpressionVisitor(sourceBuilder)

    override fun visitCElement(element: CElement) {
        m.error("Unable to serialize element: ${element::class.simpleName}", element)
    }

    override fun visitCFile(file: CFile) {
        file.declarations.forEach { it.accept(this) }
    }

    override fun visitCDeclaration(declaration: CDeclaration) {
        m.error("Unable to serialize declaration $declaration: Found ${declaration::class.simpleName}", declaration)
    }

    override fun visitSBasicClass(basicClass: SBasicClass) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("class $basicClass;", basicClass)
        sourceBuilder.indent {
            basicClass.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass: $basicClass", basicClass)
    }

    override fun visitSModule(module: SModule) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("module $module;", module)
        sourceBuilder.indent {
            module.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule: $module", module)
    }

    override fun visitSFunction(function: SFunction) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(function)
        sourceBuilder.appendLine("function $typeString $function();", function)
        val bodyBlockExpression = function.bodyBlockExpression
        if (bodyBlockExpression != null) {
            sourceBuilder.indent {
                expressionVisitor.serializeAsStatement(bodyBlockExpression)
            }
        }
        sourceBuilder.appendLine("endfunction: $function", function)
    }

    override fun visitSProperty(property: SProperty) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(property)
        sourceBuilder.append("$typeString $property", property)
        val initializer = property.initializer
        if (initializer != null) {
            sourceBuilder.append(" = ", property)
            expressionVisitor.serializeAsExpression(initializer)
            sourceBuilder.appendLine(";", initializer)
        } else {
            sourceBuilder.appendLine(";", property)
        }
    }

    override fun visitSInitialBlock(initialBlock: SInitialBlock) {
        appendLineIfNotFirst()
        sourceBuilder.append("initial ", initialBlock)
        expressionVisitor.serializeAsStatement(initialBlock.bodyBlockExpression)
    }

    private fun appendLineIfNotFirst() {
        if (first) {
            first = false
        } else {
            sourceBuilder.appendLine()
        }
    }
}