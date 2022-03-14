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

import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.declaration.sv.EComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.declaration.sv.ECoverBin
import io.verik.compiler.ast.element.declaration.sv.ECoverCross
import io.verik.compiler.ast.element.declaration.sv.ECoverGroup
import io.verik.compiler.ast.element.declaration.sv.ECoverPoint
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.EInitialBlock
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.declaration.sv.EPort
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.ESvAbstractFunction
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.declaration.sv.ETypeDefinition
import io.verik.compiler.ast.element.declaration.sv.EUnion
import io.verik.compiler.ast.property.PortKind
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

object DeclarationSerializer {

    fun serializeTypeDefinition(typeDefinition: ETypeDefinition, serializeContext: SerializeContext) {
        val serializedType = TypeSerializer.serialize(typeDefinition.type, typeDefinition)
        serializeContext.append("typedef ${serializedType.base} ")
        serializeContext.appendLine("${typeDefinition.name} ${serializedType.variableDimension};")
    }

    fun serializeInjectedProperty(injectedProperty: EInjectedProperty, serializeContext: SerializeContext) {
        serializeContext.serializeAsStatement(injectedProperty.injectedExpression)
    }

    fun serializeClass(cls: ESvClass, serializeContext: SerializeContext) {
        serializeContext.append("class ${cls.name}")
        val superType = cls.superType
        if (superType.reference != Target.C_Void) {
            val serializedSuperType = TypeSerializer.serialize(superType, cls)
            serializedSuperType.checkNoVariableDimension(cls)
            serializeContext.append(" extends ${serializedSuperType.base}")
        }
        serializeContext.appendLine(";")
        serializeContext.indent {
            cls.declarations.forEach { serializeContext.serializeAsDeclaration(it) }
            serializeContext.appendLine()
        }
        serializeContext.label(cls.bodyEndLocation) {
            serializeContext.appendLine("endclass : ${cls.name}")
        }
    }

    fun serializeCoverGroup(coverGroup: ECoverGroup, serializeContext: SerializeContext) {
        serializeContext.append("covergroup ${coverGroup.name}")
        serializeValueParameterList(coverGroup.constructor, serializeContext)
        serializeContext.indent {
            coverGroup.declarations.forEach { serializeContext.serializeAsDeclaration(it) }
            serializeContext.appendLine()
        }
        serializeContext.label(coverGroup.bodyEndLocation) {
            serializeContext.appendLine("endgroup : ${coverGroup.name}")
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
        serializeContext.append("typedef enum")
        val property = enum.property
        if (property != null) {
            val serializedType = TypeSerializer.serialize(property.type, enum)
            serializedType.checkNoVariableDimension(enum)
            serializeContext.append(" ${serializedType.base}")
        }
        serializeContext.appendLine(" {")
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

    fun serializeUnion(union: EUnion, serializeContext: SerializeContext) {
        serializeContext.appendLine("typedef union packed {")
        serializeContext.indent {
            union.properties.forEach {
                serializeContext.serialize(it)
            }
        }
        serializeContext.label(union.bodyEndLocation) {
            serializeContext.appendLine("} ${union.name};")
        }
    }

    fun serializeFunction(function: ESvFunction, serializeContext: SerializeContext) {
        if (function.isStatic) serializeContext.append("static ")
        if (function.isVirtual) serializeContext.append("virtual ")
        val serializedType = TypeSerializer.serialize(function.type, function)
        serializedType.checkNoVariableDimension(function)
        serializeContext.append("function automatic ${serializedType.base} ${function.name}")
        serializeValueParameterList(function, serializeContext)
        serializeContext.indent {
            function.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(function.body.endLocation) {
            serializeContext.appendLine("endfunction : ${function.name}")
        }
    }

    fun serializeTask(task: ETask, serializeContext: SerializeContext) {
        serializeContext.append("task automatic ${task.name}")
        serializeValueParameterList(task, serializeContext)
        serializeContext.indent {
            task.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(task.body.endLocation) {
            serializeContext.appendLine("endtask : ${task.name}")
        }
    }

    fun serializeConstructor(constructor: ESvConstructor, serializeContext: SerializeContext) {
        serializeContext.append("function new")
        serializeValueParameterList(constructor, serializeContext)
        serializeContext.indent {
            constructor.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(constructor.body.endLocation) {
            serializeContext.appendLine("endfunction : new")
        }
    }

    fun serializeInitialBlock(initialBlock: EInitialBlock, serializeContext: SerializeContext) {
        serializeContext.appendLine("initial begin : ${initialBlock.name}")
        serializeContext.indent {
            initialBlock.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(initialBlock.body.endLocation) {
            serializeContext.appendLine("end : ${initialBlock.name}")
        }
    }

    fun serializeAlwaysComBlock(alwaysComBlock: EAlwaysComBlock, serializeContext: SerializeContext) {
        serializeContext.appendLine("always_comb begin : ${alwaysComBlock.name}")
        serializeContext.indent {
            alwaysComBlock.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(alwaysComBlock.body.endLocation) {
            serializeContext.appendLine("end : ${alwaysComBlock.name}")
        }
    }

    fun serializeAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock, serializeContext: SerializeContext) {
        serializeContext.append("always_ff ")
        serializeContext.serializeAsExpression(alwaysSeqBlock.eventControlExpression)
        serializeContext.appendLine(" begin : ${alwaysSeqBlock.name}")
        serializeContext.indent {
            alwaysSeqBlock.body.statements.forEach { serializeContext.serializeAsStatement(it) }
        }
        serializeContext.label(alwaysSeqBlock.body.endLocation) {
            serializeContext.appendLine("end : ${alwaysSeqBlock.name}")
        }
    }

    fun serializeProperty(property: EProperty, serializeContext: SerializeContext) {
        if (property.isStatic) serializeContext.append("static ")
        when {
            property.hasAnnotationEntry(AnnotationEntries.RAND) -> serializeContext.append("rand ")
            property.hasAnnotationEntry(AnnotationEntries.RANDC) -> serializeContext.append("randc ")
        }
        serializePropertyTypeAndName(property, true, serializeContext)
        val initializer = property.initializer
        if (initializer != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(initializer)
            serializeContext.appendLine(";")
        } else {
            serializeContext.appendLine(";")
        }
    }

    fun serializeEnumEntry(enumEntry: EEnumEntry, serializeContext: SerializeContext) {
        serializeContext.append(enumEntry.name)
        val expression = enumEntry.expression
        if (expression != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(expression)
        }
    }

    fun serializeCoverPoint(coverPoint: ECoverPoint, serializeContext: SerializeContext) {
        serializeContext.append("${coverPoint.name} : coverpoint ")
        serializeContext.serializeAsExpression(coverPoint.expression)
        serializeCoverBins(coverPoint.coverBins, coverPoint.endLocation, serializeContext)
    }

    fun serializeCoverCross(coverCross: ECoverCross, serializeContext: SerializeContext) {
        serializeContext.append("${coverCross.name} : cross ")
        serializeContext.append(coverCross.coverPoints.joinToString { it.name })
        serializeCoverBins(coverCross.coverBins, coverCross.endLocation, serializeContext)
    }

    fun serializeCoverBin(coverBin: ECoverBin, serializeContext: SerializeContext) {
        if (coverBin.isIgnored) {
            serializeContext.append("ignore_bins ")
        } else {
            serializeContext.append("bins ")
        }
        serializeContext.append(coverBin.name)
        if (coverBin.isArray) serializeContext.append("[]")
        serializeContext.append(" = ")
        serializeContext.serializeAsExpression(coverBin.expression)
        serializeContext.appendLine(";")
    }

    fun serializeComponentInstantiation(
        componentInstantiation: EComponentInstantiation,
        serializeContext: SerializeContext
    ) {
        val serializedType = TypeSerializer.serialize(componentInstantiation.type, componentInstantiation)
        serializedType.checkNoVariableDimension(componentInstantiation)
        serializeContext.append("${serializedType.base} ${componentInstantiation.name} (")
        if (componentInstantiation.valueArguments.isNotEmpty()) {
            serializeContext.appendLine()
            serializeContext.indent {
                val portsAndValueArguments = componentInstantiation.getPorts()
                    .zip(componentInstantiation.valueArguments)
                serializeContext.serializeJoinAppendLine(portsAndValueArguments) { (port, valueArgument) ->
                    serializeContext.label(valueArgument.location) {
                        serializeContext.append(".${port.name} ")
                        serializeContext.align()
                        serializeContext.append("( ")
                        serializeContext.serializeAsExpression(valueArgument)
                        serializeContext.append(" )")
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
        if (modulePortInstantiation.valueArguments.isEmpty()) {
            serializeContext.appendLine(";")
        } else {
            serializeContext.appendLine(" (")
            serializeContext.indent {
                val portsAndValueArguments = modulePortInstantiation.getPorts()
                    .zip(modulePortInstantiation.valueArguments)
                serializeContext.serializeJoinAppendLine(portsAndValueArguments) { (port, valueArgument) ->
                    serializeContext.label(valueArgument.location) {
                        serializePortKind(port.kind, serializeContext)
                        serializeContext.append(port.name)
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
            val portsAndValueArguments = clockingBlockInstantiation.getPorts()
                .zip(clockingBlockInstantiation.valueArguments)
            portsAndValueArguments.forEach { (port, valueArgument) ->
                serializeContext.label(valueArgument.location) {
                    serializePortKind(port.kind, serializeContext)
                    serializeContext.appendLine("${port.name};")
                }
            }
        }
        serializeContext.label(clockingBlockInstantiation.endLocation) {
            serializeContext.appendLine("endclocking")
        }
    }

    fun serializeConstraint(constraint: EConstraint, serializeContext: SerializeContext) {
        serializeContext.appendLine("constraint ${constraint.name} {")
        serializeContext.indent {
            constraint.body.statements.forEach {
                serializeContext.serializeAsExpression(it)
                serializeContext.appendLine(";")
            }
        }
        serializeContext.label(constraint.body.endLocation) {
            serializeContext.appendLine("}")
        }
    }

    fun serializeValueParameter(valueParameter: ESvValueParameter, serializeContext: SerializeContext) {
        when (valueParameter.kind) {
            ValueParameterKind.INPUT -> serializeContext.append("input ")
            ValueParameterKind.OUTPUT -> serializeContext.append("output ")
            ValueParameterKind.REF -> serializeContext.append("ref ")
        }
        serializePropertyTypeAndName(valueParameter, true, serializeContext)
        val expression = valueParameter.expression
        if (expression != null) {
            serializeContext.append(" = ")
            serializeContext.serializeAsExpression(expression)
        }
    }

    fun serializePort(port: EPort, serializeContext: SerializeContext) {
        serializePortKind(port.kind, serializeContext)
        serializePropertyTypeAndName(port, false, serializeContext)
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

    private fun serializePortKind(kind: PortKind, serializeContext: SerializeContext) {
        when (kind) {
            PortKind.INPUT -> serializeContext.append("input  ")
            PortKind.OUTPUT -> serializeContext.append("output ")
            PortKind.MODULE_INTERFACE, PortKind.MODULE_PORT -> {}
            PortKind.CLOCKING_BLOCK -> serializeContext.append("clocking ")
        }
    }

    private fun serializePropertyTypeAndName(
        abstractProperty: EAbstractProperty,
        isSerializeVirtual: Boolean,
        serializeContext: SerializeContext
    ) {
        val serializedType = TypeSerializer.serialize(abstractProperty.type, abstractProperty)
        if (isSerializeVirtual && serializedType.isVirtual) {
            serializeContext.append("virtual ")
        }
        serializeContext.append("${serializedType.base} ")
        serializeContext.align()
        serializeContext.append(abstractProperty.name)
        if (serializedType.variableDimension != null) {
            serializeContext.append(" ${serializedType.variableDimension}")
        }
    }

    private fun serializeValueParameterList(
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

    private fun serializeCoverBins(
        coverBins: List<ECoverBin>,
        endLocation: SourceLocation,
        serializeContext: SerializeContext
    ) {
        if (coverBins.isNotEmpty()) {
            serializeContext.appendLine(" {")
            serializeContext.indent {
                coverBins.forEach { serializeContext.serializeAsDeclaration(it) }
                serializeContext.appendLine()
            }
            serializeContext.label(endLocation) {
                serializeContext.appendLine("}")
            }
        } else {
            serializeContext.appendLine(";")
        }
    }
}
