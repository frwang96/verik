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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.PortType

object DeclarationSerializer {

    fun serializeSvBasicClass(basicClass: ESvBasicClass, serializerContext: SerializerContext) {
        serializerContext.appendLine("class ${basicClass.name};")
        serializerContext.indent {
            basicClass.members.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.appendLine()
        }
        serializerContext.appendLine("endclass : ${basicClass.name}")
    }

    fun serializeModule(module: EModule, serializerContext: SerializerContext) {
        serializerContext.append("module ${module.name}")
        if (module.ports.isNotEmpty()) {
            serializerContext.appendLine("(")
            serializerContext.indent {
                serializerContext.joinLine(module.ports) {
                    serializerContext.serialize(it)
                }
            }
            serializerContext.appendLine(");")
        } else {
            serializerContext.appendLine(";")
        }
        serializerContext.indent {
            module.members.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.appendLine()
        }
        serializerContext.appendLine("endmodule : ${module.name}")
    }

    fun serializeEnum(enum: EEnum, serializerContext: SerializerContext) {
        serializerContext.appendLine("typedef enum {")
        serializerContext.indent {
            val entryReferences = enum.entryReferences.mapNotNull { it.cast<ESvEnumEntry>(enum) }
            serializerContext.joinLine(entryReferences) {
                serializerContext.serialize(it)
            }
        }
        serializerContext.appendLine("} ${enum.name};")
    }

    fun serializeStruct(struct: EStruct, serializerContext: SerializerContext) {
        serializerContext.appendLine("typedef struct packed {")
        serializerContext.indent {
            struct.properties.forEach {
                serializerContext.serialize(it)
            }
        }
        serializerContext.appendLine("} ${struct.name};")
    }

    fun serializeSvFunction(function: ESvFunction, serializerContext: SerializerContext) {
        val typeString = TypeSerializer.serialize(function.type, function)
        if (function.isStatic)
            serializerContext.append("static ")
        else
            serializerContext.append("automatic ")
        serializerContext.append("function $typeString ${function.name}(")
        if (function.valueParameters.isNotEmpty()) {
            serializerContext.softBreak()
            serializerContext.join(function.valueParameters) {
                serializerContext.serialize(it)
            }
        }
        serializerContext.appendLine(");")
        val body = function.body
        if (body != null) {
            serializerContext.indent {
                serializerContext.serializeAsStatement(body)
            }
        }
        serializerContext.appendLine("endfunction : ${function.name}")
    }

    fun serializeInitialBlock(initialBlock: EInitialBlock, serializerContext: SerializerContext) {
        serializerContext.append("initial ")
        serializerContext.serializeAsStatement(initialBlock.getBodyNotNull())
    }

    fun serializeAlwaysComBlock(alwaysComBlock: EAlwaysComBlock, serializerContext: SerializerContext) {
        serializerContext.append("always_comb ")
        serializerContext.serializeAsStatement(alwaysComBlock.getBodyNotNull())
    }

    fun serializeAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock, serializerContext: SerializerContext) {
        serializerContext.append("always_ff ")
        serializerContext.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        serializerContext.append(" ")
        serializerContext.serializeAsStatement(alwaysSeqBlock.getBodyNotNull())
    }

    fun serializeSvProperty(property: ESvProperty, serializerContext: SerializerContext) {
        val typeString = TypeSerializer.serialize(property.type, property)
        serializerContext.append("$typeString ${property.name}")
        val initializer = property.initializer
        if (initializer != null) {
            serializerContext.append(" ")
            serializerContext.align()
            serializerContext.append("= ")
            serializerContext.serializeAsExpression(initializer)
            serializerContext.appendLine(";")
        } else {
            serializerContext.appendLine(";")
        }
    }

    fun serializeSvEnumEntry(enumEntry: ESvEnumEntry, serializerContext: SerializerContext) {
        serializerContext.append(enumEntry.name)
    }

    fun serializeModuleInstantiation(
        moduleInstantiation: EModuleInstantiation,
        serializerContext: SerializerContext
    ) {
        val typeString = TypeSerializer.serialize(moduleInstantiation.type, moduleInstantiation)
        serializerContext.append("$typeString ${moduleInstantiation.name} (")
        if (moduleInstantiation.portInstantiations.isNotEmpty()) {
            serializerContext.appendLine()
            serializerContext.indent {
                serializerContext.joinLine(moduleInstantiation.portInstantiations) {
                    serializerContext.append(".${it.reference.name} ")
                    serializerContext.align()
                    serializerContext.append("( ")
                    val expression = it.expression
                    if (expression != null) {
                        serializerContext.serializeAsExpression(expression)
                        serializerContext.append(" )")
                    } else {
                        serializerContext.append(")")
                    }
                }
            }
        }
        serializerContext.appendLine(");")
    }

    fun serializeValueParameter(valueParameter: ESvValueParameter, serializerContext: SerializerContext) {
        val typeString = TypeSerializer.serialize(valueParameter.type, valueParameter)
        serializerContext.append("$typeString ${valueParameter.name}")
    }

    fun serializePort(port: EPort, serializerContext: SerializerContext) {
        when (port.portType) {
            PortType.INPUT -> serializerContext.append("input ")
            PortType.OUTPUT -> serializerContext.append("output ")
        }
        val typeString = TypeSerializer.serialize(port.type, port)
        serializerContext.append("$typeString ")
        serializerContext.align()
        serializerContext.append(port.name)
    }
}
