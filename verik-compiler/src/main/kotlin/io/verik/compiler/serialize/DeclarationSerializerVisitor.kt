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
import io.verik.compiler.ast.element.common.EValueParameter
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages

class DeclarationSerializerVisitor(private val sourceBuilder: SourceBuilder) : Visitor() {

    private var lastAppendLineType = AppendLineType.FIRST
    private val expressionSerializerVisitor = ExpressionSerializerVisitor(sourceBuilder)

    fun serializeAsDeclaration(element: EElement) {
        sourceBuilder.label(element) {
            if (element !is Declaration)
                Messages.INTERNAL_ERROR.on(element, "Declaration expected but got: $element")
            element.accept(this)
        }
    }

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element as declaration: $element")
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("class ${basicClass.name};")
        sourceBuilder.indent {
            basicClass.members.forEach { serializeAsDeclaration(it) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass : ${basicClass.name}")
    }

    override fun visitModule(module: EModule) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("module ${module.name};")
        sourceBuilder.indent {
            module.members.forEach { serializeAsDeclaration(it) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule : ${module.name}")
    }

    override fun visitEnum(enum: EEnum) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.appendLine("typedef enum {")
        sourceBuilder.indent {
            val entryReferences = enum.entryReferences.mapNotNull { it.cast<ESvEnumEntry>(enum) }
            sourceBuilder.label(entryReferences[0]) { sourceBuilder.append(entryReferences[0].name) }
            entryReferences.drop(1).forEach {
                sourceBuilder.appendLine(",")
                sourceBuilder.label(it) { sourceBuilder.append(it.name) }
            }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("} ${enum.name};")
    }

    override fun visitSvFunction(function: ESvFunction) {
        appendLine(AppendLineType.REGULAR)
        val typeString = TypeSerializer.serialize(function.returnType, function)
        sourceBuilder.append("function $typeString ${function.name}(")
        if (function.valueParameters.isNotEmpty()) {
            serializeAsDeclaration(function.valueParameters[0])
            function.valueParameters.drop(1).forEach {
                sourceBuilder.append(", ")
                serializeAsDeclaration(it)
            }
        }
        sourceBuilder.appendLine(");")
        val body = function.body
        if (body != null) {
            sourceBuilder.indent {
                expressionSerializerVisitor.serializeAsStatement(body)
            }
        }
        sourceBuilder.appendLine("endfunction : ${function.name}")
    }

    override fun visitSvProperty(property: ESvProperty) {
        appendLine(AppendLineType.PROPERTY)
        val typeString = TypeSerializer.serialize(property.type, property)
        sourceBuilder.append("$typeString ${property.name}")
        val initializer = property.initializer
        if (initializer != null) {
            sourceBuilder.append(" ")
            sourceBuilder.align()
            sourceBuilder.append("= ")
            expressionSerializerVisitor.serializeAsExpression(initializer)
            sourceBuilder.appendLine(";")
        } else {
            sourceBuilder.appendLine(";")
        }
    }

    // Enum entries are serialized as part of the parent enum
    override fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {}

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("initial ")
        expressionSerializerVisitor.serializeAsStatement(initialBlock.body)
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("always_comb ")
        expressionSerializerVisitor.serializeAsStatement(alwaysComBlock.body)
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        appendLine(AppendLineType.REGULAR)
        sourceBuilder.append("always_ff ")
        expressionSerializerVisitor.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        sourceBuilder.append(" ")
        expressionSerializerVisitor.serializeAsStatement(alwaysSeqBlock.body)
    }

    override fun visitValueParameter(valueParameter: EValueParameter) {
        val typeString = TypeSerializer.serialize(valueParameter.type, valueParameter)
        sourceBuilder.append("$typeString ${valueParameter.name}")
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
