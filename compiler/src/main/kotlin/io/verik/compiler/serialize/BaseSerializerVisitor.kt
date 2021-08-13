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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.m

class BaseSerializerVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    private var lastAppendLineType = AppendLineType.FIRST
    private val expressionSerializerVisitor = ExpressionSerializerVisitor(sourceBuilder)

    override fun visitElement(element: EElement) {
        m.error("Unable to serialize element: $element", element)
    }

    override fun visitFile(file: EFile) {
        file.members.forEach { it.accept(this) }
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("class ${basicClass.name};", basicClass)
        sourceBuilder.indent {
            basicClass.members.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass : ${basicClass.name}", basicClass)
    }

    override fun visitModule(module: EModule) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("module ${module.name};", module)
        sourceBuilder.indent {
            module.members.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule : ${module.name}", module)
    }

    override fun visitEnum(enum: EEnum) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("typedef enum {", enum)
        sourceBuilder.indent {
            val entryReferences = enum.entryReferences.mapNotNull { it.cast<ESvEnumEntry>(enum) }
            entryReferences.dropLast(1).forEach {
                sourceBuilder.appendLine("${it.name},", it)
            }
            sourceBuilder.appendLine(entryReferences.last().name, entryReferences.last())
        }
        sourceBuilder.appendLine("} ${enum.name};", enum)
    }

    override fun visitSvFunction(function: ESvFunction) {
        appendLine(AppendLineType.REGULAR)
        val typeString = TypeSerializer.serialize(function.returnType, function)
        sourceBuilder.appendLine("function $typeString ${function.name}();", function)
        val body = function.body
        if (body != null) {
            sourceBuilder.indent {
                expressionSerializerVisitor.serializeAsStatement(body)
            }
        }
        sourceBuilder.appendLine("endfunction : ${function.name}", function)
    }

    override fun visitSvProperty(property: ESvProperty) {
        appendLine(AppendLineType.PROPERTY)
        val typeString = TypeSerializer.serialize(property.type, property)
        sourceBuilder.append("$typeString ${property.name}", property)
        val initializer = property.initializer
        if (initializer != null) {
            sourceBuilder.append(" ", property)
            sourceBuilder.align()
            sourceBuilder.append("= ", property)
            expressionSerializerVisitor.serializeAsExpression(initializer)
            sourceBuilder.appendLine(";", initializer)
        } else {
            sourceBuilder.appendLine(";", property)
        }
    }

    // Enum entries are serialized through the parent enum
    override fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {}

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("initial ", initialBlock)
        expressionSerializerVisitor.serializeAsStatement(initialBlock.body)
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("always_comb ", alwaysComBlock)
        expressionSerializerVisitor.serializeAsStatement(alwaysComBlock.body)
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("always_ff ", alwaysSeqBlock)
        expressionSerializerVisitor.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        sourceBuilder.append(" ", alwaysSeqBlock)
        expressionSerializerVisitor.serializeAsStatement(alwaysSeqBlock.body)
    }

    private fun appendLine(appendLineType: AppendLineType) {
        when {
            lastAppendLineType == AppendLineType.PROPERTY && appendLineType == AppendLineType.REGULAR ->
                sourceBuilder.appendLine()
            lastAppendLineType == AppendLineType.REGULAR && appendLineType == AppendLineType.PROPERTY ->
                sourceBuilder.appendLine()
            lastAppendLineType == AppendLineType.REGULAR && appendLineType == AppendLineType.REGULAR ->
                sourceBuilder.appendLine()
        }
        lastAppendLineType = appendLineType
    }

    enum class AppendLineType { FIRST, PROPERTY, REGULAR }
}