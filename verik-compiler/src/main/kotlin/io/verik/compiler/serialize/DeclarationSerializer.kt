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

import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.interfaces.cast

object DeclarationSerializer {

    fun serializeSvBasicClass(basicClass: ESvBasicClass, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.appendLine("class ${basicClass.name};")
        sourceSerializerContext.indent {
            basicClass.members.forEach { sourceSerializerContext.serializeAsDeclaration(it) }
            sourceSerializerContext.appendLine()
        }
        sourceSerializerContext.appendLine("endclass : ${basicClass.name}")
    }

    fun serializeModule(module: EModule, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.appendLine("module ${module.name};")
        sourceSerializerContext.indent {
            module.members.forEach { sourceSerializerContext.serializeAsDeclaration(it) }
            sourceSerializerContext.appendLine()
        }
        sourceSerializerContext.appendLine("endmodule : ${module.name}")
    }

    fun serializeEnum(enum: EEnum, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.appendLine("typedef enum {")
        sourceSerializerContext.indent {
            val entryReferences = enum.entryReferences.mapNotNull { it.cast<ESvEnumEntry>(enum) }
            sourceSerializerContext.joinLine(entryReferences) {
                sourceSerializerContext.serialize(it)
            }
        }
        sourceSerializerContext.appendLine("} ${enum.name};")
    }

    fun serializeSvFunction(function: ESvFunction, sourceSerializerContext: SourceSerializerContext) {
        val typeString = TypeSerializer.serialize(function.returnType, function)
        sourceSerializerContext.append("function $typeString ${function.name}(")
        if (function.valueParameters.isNotEmpty()) {
            sourceSerializerContext.softBreak()
            sourceSerializerContext.join(function.valueParameters) {
                sourceSerializerContext.serialize(it)
            }
        }
        sourceSerializerContext.appendLine(");")
        val body = function.body
        if (body != null) {
            sourceSerializerContext.indent {
                sourceSerializerContext.serializeAsStatement(body)
            }
        }
        sourceSerializerContext.appendLine("endfunction : ${function.name}")
    }

    fun serializeSvProperty(property: ESvProperty, sourceSerializerContext: SourceSerializerContext) {
        val typeString = TypeSerializer.serialize(property.type, property)
        sourceSerializerContext.append("$typeString ${property.name}")
        val initializer = property.initializer
        if (initializer != null) {
            sourceSerializerContext.append(" ")
            sourceSerializerContext.align()
            sourceSerializerContext.append("= ")
            sourceSerializerContext.serializeAsExpression(initializer)
            sourceSerializerContext.appendLine(";")
        } else {
            sourceSerializerContext.appendLine(";")
        }
    }

    fun serializeSvEnumEntry(enumEntry: ESvEnumEntry, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append(enumEntry.name)
    }

    fun serializeInitialBlock(initialBlock: EInitialBlock, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append("initial ")
        sourceSerializerContext.serializeAsStatement(initialBlock.body)
    }

    fun serializeAlwaysComBlock(alwaysComBlock: EAlwaysComBlock, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append("always_comb ")
        sourceSerializerContext.serializeAsStatement(alwaysComBlock.body)
    }

    fun serializeAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock, sourceSerializerContext: SourceSerializerContext) {
        sourceSerializerContext.append("always_ff ")
        sourceSerializerContext.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        sourceSerializerContext.append(" ")
        sourceSerializerContext.serializeAsStatement(alwaysSeqBlock.body)
    }

    fun serializeValueParameter(valueParameter: ESvValueParameter, sourceSerializerContext: SourceSerializerContext) {
        val typeString = TypeSerializer.serialize(valueParameter.type, valueParameter)
        sourceSerializerContext.append("$typeString ${valueParameter.name}")
    }
}
