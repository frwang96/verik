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
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EComponentInstantiation
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedProperty
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.FunctionQualifierType
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.core.common.Core

object DeclarationSerializer {

    fun serializeTypeDefinition(typeDefinition: ETypeDefinition, serializerContext: SerializerContext) {
        val serializedType = TypeSerializer.serialize(typeDefinition.type, typeDefinition)
        serializerContext.append("typedef ${serializedType.getBaseAndPackedDimension()} ")
        serializerContext.appendLine("${typeDefinition.name} ${serializedType.unpackedDimension};")
    }

    fun serializeInjectedProperty(injectedProperty: EInjectedProperty, serializerContext: SerializerContext) {
        injectedProperty.entries.forEach {
            when (it) {
                is LiteralStringEntry -> {
                    if (it.text == "\n")
                        serializerContext.appendLine()
                    else
                        serializerContext.append(it.text)
                }
                is ExpressionStringEntry ->
                    serializerContext.serializeAsExpression(it.expression)
            }
        }
        serializerContext.appendLine()
    }

    fun serializeSvClass(`class`: ESvClass, serializerContext: SerializerContext) {
        if (`class`.isVirtual)
            serializerContext.append("virtual ")
        serializerContext.append("class ${`class`.name}")
        val superType = `class`.superType
        if (superType.reference != Core.Kt.C_Any) {
            serializerContext.append(" extends ${superType.reference.name}")
        }
        serializerContext.appendLine(";")
        serializerContext.indent {
            `class`.declarations.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.appendLine()
        }
        serializerContext.appendLine("endclass : ${`class`.name}")
    }

    fun serializeModule(module: EModule, serializerContext: SerializerContext) {
        serializerContext.append("module ${module.name}")
        serializePortList(module.ports, serializerContext)
        serializerContext.indent {
            module.declarations.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.appendLine()
        }
        serializerContext.appendLine("endmodule : ${module.name}")
    }

    fun serializeModuleInterface(moduleInterface: EModuleInterface, serializerContext: SerializerContext) {
        serializerContext.append("interface ${moduleInterface.name}")
        serializePortList(moduleInterface.ports, serializerContext)
        serializerContext.indent {
            moduleInterface.declarations.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.appendLine()
        }
        serializerContext.appendLine("endinterface : ${moduleInterface.name}")
    }

    fun serializeEnum(enum: EEnum, serializerContext: SerializerContext) {
        serializerContext.appendLine("typedef enum {")
        serializerContext.indent {
            serializerContext.serializeJoinAppendLine(enum.enumEntries) {
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
        when (function.qualifierType) {
            FunctionQualifierType.VIRTUAL -> serializerContext.append("virtual ")
            FunctionQualifierType.PURE_VIRTUAL -> serializerContext.append("pure virtual ")
            else -> if (function.isStatic) serializerContext.append("static ")
        }
        val serializedType = TypeSerializer.serialize(function.type, function)
        serializedType.checkNoUnpackedDimension(function)
        serializerContext.append("function automatic ${serializedType.getBaseAndPackedDimension()} ${function.name}")
        serializeSvValueParameterList(function.valueParameters, serializerContext)
        if (function.qualifierType != FunctionQualifierType.PURE_VIRTUAL) {
            val body = function.body
            if (body != null) {
                serializerContext.indent {
                    serializerContext.serializeAsStatement(body)
                }
            }
            serializerContext.appendLine("endfunction : ${function.name}")
        }
    }

    fun serializeTask(task: ETask, serializerContext: SerializerContext) {
        serializerContext.append("task automatic ${task.name}")
        serializeSvValueParameterList(task.valueParameters, serializerContext)
        val body = task.body
        if (body != null) {
            serializerContext.indent {
                serializerContext.serializeAsStatement(body)
            }
        }
        serializerContext.appendLine("endtask : ${task.name}")
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
        when (property.isStatic) {
            true -> serializerContext.append("static ")
            false -> serializerContext.append("automatic ")
        }
        if (property.isVirtual())
            serializerContext.append("virtual ")
        val serializedType = TypeSerializer.serialize(property.type, property)
        serializerContext.append(serializedType.getBaseAndPackedDimension() + " ")
        serializerContext.align()
        serializerContext.append(property.name)
        if (serializedType.unpackedDimension != null)
            serializerContext.append(" ${serializedType.unpackedDimension}")
        val initializer = property.initializer
        if (initializer != null) {
            serializerContext.append(" = ")
            serializerContext.serializeAsExpression(initializer)
            serializerContext.appendLine(";")
        } else {
            serializerContext.appendLine(";")
        }
    }

    fun serializeSvEnumEntry(enumEntry: ESvEnumEntry, serializerContext: SerializerContext) {
        serializerContext.append(enumEntry.name)
    }

    fun serializeComponentInstantiation(
        componentInstantiation: EComponentInstantiation,
        serializerContext: SerializerContext
    ) {
        val serializedType = TypeSerializer.serialize(componentInstantiation.type, componentInstantiation)
        serializedType.checkNoPackedDimension(componentInstantiation)
        serializedType.checkNoUnpackedDimension(componentInstantiation)
        serializerContext.append("${serializedType.base} ${componentInstantiation.name} (")
        if (componentInstantiation.portInstantiations.isNotEmpty()) {
            serializerContext.appendLine()
            serializerContext.indent {
                serializerContext.serializeJoinAppendLine(componentInstantiation.portInstantiations) {
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

    fun serializeModulePortInstantiation(
        modulePortInstantiation: EModulePortInstantiation,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("modport ${modulePortInstantiation.name}")
        if (modulePortInstantiation.portInstantiations.isEmpty()) {
            serializerContext.appendLine(";")
        } else {
            serializerContext.appendLine(" (")
            serializerContext.indent {
                serializerContext.serializeJoinAppendLine(modulePortInstantiation.portInstantiations) {
                    serializePortType(it.portType, serializerContext)
                    serializerContext.append(it.reference.name)
                }
            }
            serializerContext.appendLine(");")
        }
    }

    fun serializeClockingBlockInstantiation(
        clockingBlockInstantiation: EClockingBlockInstantiation,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("clocking ${clockingBlockInstantiation.name} ")
        serializerContext.serializeAsExpression(clockingBlockInstantiation.eventControlExpression)
        serializerContext.appendLine(";")
        serializerContext.indent {
            clockingBlockInstantiation.portInstantiations.forEach {
                serializePortType(it.portType, serializerContext)
                serializerContext.appendLine("${it.reference.name};")
            }
        }
        serializerContext.appendLine("endclocking")
    }

    fun serializeSvValueParameter(valueParameter: ESvValueParameter, serializerContext: SerializerContext) {
        when (valueParameter.isInput) {
            true -> serializerContext.append("input ")
            false -> serializerContext.append("output ")
        }
        if (valueParameter.isVirtual())
            serializerContext.append("virtual ")
        val serializedType = TypeSerializer.serialize(valueParameter.type, valueParameter)
        serializerContext.append("${serializedType.getBaseAndPackedDimension()} ${valueParameter.name}")
        if (serializedType.unpackedDimension != null)
            serializerContext.append(" ${serializedType.unpackedDimension}")
    }

    fun serializePort(port: EPort, serializerContext: SerializerContext) {
        serializePortType(port.portType, serializerContext)
        val serializedType = TypeSerializer.serialize(port.type, port)
        serializerContext.append("${serializedType.getBaseAndPackedDimension()} ")
        serializerContext.align()
        serializerContext.append(port.name)
        if (serializedType.unpackedDimension != null)
            serializerContext.append(" ${serializedType.unpackedDimension}")
    }

    private fun serializePortList(ports: List<EPort>, serializerContext: SerializerContext) {
        if (ports.isNotEmpty()) {
            serializerContext.appendLine("(")
            serializerContext.indent {
                serializerContext.serializeJoinAppendLine(ports) {
                    serializerContext.serialize(it)
                }
            }
            serializerContext.appendLine(");")
        } else {
            serializerContext.appendLine(";")
        }
    }

    private fun serializePortType(portType: PortType, serializerContext: SerializerContext) {
        when (portType) {
            PortType.INPUT -> serializerContext.append("input ")
            PortType.OUTPUT -> serializerContext.append("output ")
            PortType.MODULE_INTERFACE, PortType.MODULE_PORT -> {}
            PortType.CLOCKING_BLOCK -> serializerContext.append("clocking ")
        }
    }

    private fun serializeSvValueParameterList(
        valueParameters: List<ESvValueParameter>,
        serializerContext: SerializerContext
    ) {
        serializerContext.append("(")
        if (valueParameters.isNotEmpty()) {
            serializerContext.softBreak()
            serializerContext.serializeJoin(valueParameters) {
                serializerContext.serialize(it)
            }
        }
        serializerContext.appendLine(");")
    }
}
