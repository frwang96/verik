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

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
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
import io.verik.compiler.ast.element.sv.ESvAbstractFunction
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

    fun serializeTypeDefinition(typeDefinition: ETypeDefinition, serializeContext: SerializeContext) {
        val serializedType = TypeSerializer.serialize(typeDefinition.type, typeDefinition)
        serializeContext.append("typedef ${serializedType.getBaseAndPackedDimension()} ")
        serializeContext.appendLine("${typeDefinition.name} ${serializedType.unpackedDimension};")
    }

    fun serializeInjectedProperty(injectedProperty: EInjectedProperty, serializeContext: SerializeContext) {
        injectedProperty.entries.forEach {
            when (it) {
                is LiteralStringEntry -> {
                    if (it.text == "\n")
                        serializeContext.appendLine()
                    else
                        serializeContext.append(it.text)
                }
                is ExpressionStringEntry ->
                    serializeContext.serializeAsExpression(it.expression)
            }
        }
        serializeContext.appendLine()
    }

    fun serializeSvClass(`class`: ESvClass, serializeContext: SerializeContext) {
        if (`class`.isVirtual)
            serializeContext.append("virtual ")
        serializeContext.append("class ${`class`.name}")
        val superType = `class`.superType
        if (superType.reference != Core.Kt.C_Any) {
            serializeContext.append(" extends ${superType.reference.name}")
        }
        serializeContext.appendLine(";")
        serializeContext.indent {
            `class`.declarations.forEach { serializeContext.serializeAsDeclaration(it) }
            serializeContext.appendLine()
        }
        serializeContext.label(`class`.bodyEndLocation) {
            serializeContext.appendLine("endclass : ${`class`.name}")
        }
    }

    fun serializeModule(module: EModule, serializeContext: SerializeContext) {
        serializeContext.append("module ${module.name}")
        serializePortList(module, serializeContext)
        serializeContext.indent {
            module.declarations.forEach { serializeContext.serializeAsDeclaration(it) }
            serializeContext.appendLine()
        }
        serializeContext.label(module.bodyEndLocation) {
            serializeContext.appendLine("endmodule : ${module.name}")
        }
    }

    fun serializeModuleInterface(moduleInterface: EModuleInterface, serializeContext: SerializeContext) {
        serializeContext.append("interface ${moduleInterface.name}")
        serializePortList(moduleInterface, serializeContext)
        serializeContext.indent {
            moduleInterface.declarations.forEach { serializeContext.serializeAsDeclaration(it) }
            serializeContext.appendLine()
        }
        serializeContext.label(moduleInterface.bodyEndLocation) {
            serializeContext.appendLine("endinterface : ${moduleInterface.name}")
        }
    }

    fun serializeEnum(enum: EEnum, serializeContext: SerializeContext) {
        serializeContext.appendLine("typedef enum {")
        serializeContext.indent {
            serializeContext.serializeJoinAppendLine(enum.enumEntries) {
                serializeContext.serialize(it)
            }
        }
        serializeContext.label(enum.bodyEndLocation) {
            serializeContext.appendLine("} ${enum.name};")
        }
    }

    fun serializeStruct(struct: EStruct, serializeContext: SerializeContext) {
        serializeContext.appendLine("typedef struct packed {")
        serializeContext.indent {
            struct.properties.forEach {
                serializeContext.serialize(it)
            }
        }
        serializeContext.label(struct.bodyEndLocation) {
            serializeContext.appendLine("} ${struct.name};")
        }
    }

    fun serializeSvFunction(function: ESvFunction, serializeContext: SerializeContext) {
        when (function.qualifierType) {
            FunctionQualifierType.VIRTUAL -> serializeContext.append("virtual ")
            FunctionQualifierType.PURE_VIRTUAL -> serializeContext.append("pure virtual ")
            else -> if (function.isStatic) serializeContext.append("static ")
        }
        val serializedType = TypeSerializer.serialize(function.type, function)
        serializedType.checkNoUnpackedDimension(function)
        serializeContext.append("function automatic ${serializedType.getBaseAndPackedDimension()} ${function.name}")
        serializeSvValueParameterList(function, serializeContext)
        if (function.qualifierType != FunctionQualifierType.PURE_VIRTUAL) {
            serializeContext.indent {
                serializeContext.serializeAsStatement(function.body)
            }
            serializeContext.label(function.body.endLocation) {
                serializeContext.appendLine("endfunction : ${function.name}")
            }
        }
    }

    fun serializeTask(task: ETask, serializeContext: SerializeContext) {
        serializeContext.append("task automatic ${task.name}")
        serializeSvValueParameterList(task, serializeContext)
        serializeContext.indent {
            serializeContext.serializeAsStatement(task.body)
        }
        serializeContext.label(task.body.endLocation) {
            serializeContext.appendLine("endtask : ${task.name}")
        }
    }

    fun serializeInitialBlock(initialBlock: EInitialBlock, serializeContext: SerializeContext) {
        serializeContext.append("initial ")
        serializeContext.serializeAsStatement(initialBlock.body)
    }

    fun serializeAlwaysComBlock(alwaysComBlock: EAlwaysComBlock, serializeContext: SerializeContext) {
        serializeContext.append("always_comb ")
        serializeContext.serializeAsStatement(alwaysComBlock.body)
    }

    fun serializeAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock, serializeContext: SerializeContext) {
        serializeContext.append("always_ff ")
        serializeContext.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        serializeContext.append(" ")
        serializeContext.serializeAsStatement(alwaysSeqBlock.body)
    }

    fun serializeSvProperty(property: ESvProperty, serializeContext: SerializeContext) {
        when (property.isStatic) {
            true -> serializeContext.append("static ")
            false -> serializeContext.append("automatic ")
        }
        if (TypeSerializer.isVirtual(property.type))
            serializeContext.append("virtual ")
        serializePropertyTypeAndName(property, serializeContext)
        val initializer = property.initializer
        if (initializer != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(initializer)
            serializeContext.appendLine(";")
        } else {
            serializeContext.appendLine(";")
        }
    }

    fun serializeSvEnumEntry(enumEntry: ESvEnumEntry, serializeContext: SerializeContext) {
        serializeContext.append(enumEntry.name)
    }

    fun serializeComponentInstantiation(
        componentInstantiation: EComponentInstantiation,
        serializeContext: SerializeContext
    ) {
        val serializedType = TypeSerializer.serialize(componentInstantiation.type, componentInstantiation)
        serializedType.checkNoPackedDimension(componentInstantiation)
        serializedType.checkNoUnpackedDimension(componentInstantiation)
        serializeContext.append("${serializedType.base} ${componentInstantiation.name} (")
        if (componentInstantiation.portInstantiations.isNotEmpty()) {
            serializeContext.appendLine()
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(componentInstantiation.portInstantiations) {
                    serializeContext.label(it.location) {
                        serializeContext.append(".${it.port.name} ")
                        serializeContext.align()
                        serializeContext.append("( ")
                        val expression = it.expression
                        if (expression != null) {
                            serializeContext.serializeAsExpression(expression)
                            serializeContext.append(" )")
                        } else {
                            serializeContext.append(")")
                        }
                    }
                }
            }
        }
        serializeContext.label(componentInstantiation.endLocation) {
            serializeContext.appendLine(");")
        }
    }

    fun serializeModulePortInstantiation(
        modulePortInstantiation: EModulePortInstantiation,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("modport ${modulePortInstantiation.name}")
        if (modulePortInstantiation.portInstantiations.isEmpty()) {
            serializeContext.appendLine(";")
        } else {
            serializeContext.appendLine(" (")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(modulePortInstantiation.portInstantiations) {
                    serializeContext.label(it.location) {
                        serializePortType(it.port.portType, serializeContext)
                        serializeContext.append(it.port.name)
                    }
                }
            }
            serializeContext.label(modulePortInstantiation.endLocation) {
                serializeContext.appendLine(");")
            }
        }
    }

    fun serializeClockingBlockInstantiation(
        clockingBlockInstantiation: EClockingBlockInstantiation,
        serializeContext: SerializeContext
    ) {
        serializeContext.append("clocking ${clockingBlockInstantiation.name} ")
        serializeContext.serializeAsExpression(clockingBlockInstantiation.eventControlExpression)
        serializeContext.appendLine(";")
        serializeContext.indent {
            clockingBlockInstantiation.portInstantiations.forEach {
                serializeContext.label(it.location) {
                    serializePortType(it.port.portType, serializeContext)
                    serializeContext.appendLine("${it.port.name};")
                }
            }
        }
        serializeContext.label(clockingBlockInstantiation.endLocation) {
            serializeContext.appendLine("endclocking")
        }
    }

    fun serializeSvValueParameter(valueParameter: ESvValueParameter, serializeContext: SerializeContext) {
        when (valueParameter.isInput) {
            true -> serializeContext.append("input ")
            false -> serializeContext.append("output ")
        }
        if (TypeSerializer.isVirtual(valueParameter.type))
            serializeContext.append("virtual ")
        serializePropertyTypeAndName(valueParameter, serializeContext)
    }

    fun serializePort(port: EPort, serializeContext: SerializeContext) {
        serializePortType(port.portType, serializeContext)
        serializePropertyTypeAndName(port, serializeContext)
    }

    private fun serializePortList(
        abstractContainerComponent: EAbstractContainerComponent,
        serializeContext: SerializeContext
    ) {
        if (abstractContainerComponent.ports.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(abstractContainerComponent.ports) {
                    serializeContext.serialize(it)
                }
            }
            serializeContext.label(abstractContainerComponent.bodyStartLocation) {
                serializeContext.appendLine(");")
            }
        } else {
            serializeContext.appendLine(";")
        }
    }

    private fun serializePortType(portType: PortType, serializeContext: SerializeContext) {
        when (portType) {
            PortType.CONSTANT, PortType.INPUT -> serializeContext.append("input  ")
            PortType.OUTPUT -> serializeContext.append("output ")
            PortType.MODULE_INTERFACE, PortType.MODULE_PORT -> {}
            PortType.CLOCKING_BLOCK -> serializeContext.append("clocking ")
        }
    }

    private fun serializePropertyTypeAndName(
        abstractProperty: EAbstractProperty,
        serializeContext: SerializeContext
    ) {
        val serializedType = TypeSerializer.serialize(abstractProperty.type, abstractProperty)
        serializeContext.append("${serializedType.getBaseAndPackedDimension()} ")
        serializeContext.align()
        serializeContext.append(abstractProperty.name)
        if (serializedType.unpackedDimension != null)
            serializeContext.append(" ${serializedType.unpackedDimension}")
    }

    private fun serializeSvValueParameterList(
        abstractFunction: ESvAbstractFunction,
        serializeContext: SerializeContext
    ) {
        if (abstractFunction.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(abstractFunction.valueParameters) {
                    serializeContext.serialize(it)
                }
            }
            serializeContext.label(abstractFunction.body) {
                serializeContext.appendLine(");")
            }
        } else {
            serializeContext.appendLine("();")
        }
    }
}
