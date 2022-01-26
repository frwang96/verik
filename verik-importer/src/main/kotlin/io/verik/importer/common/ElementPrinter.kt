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

package io.verik.importer.common

import io.verik.importer.ast.sv.element.common.SvCompilationUnit
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.declaration.SvClass
import io.verik.importer.ast.sv.element.declaration.SvConstructor
import io.verik.importer.ast.sv.element.declaration.SvEnum
import io.verik.importer.ast.sv.element.declaration.SvEnumEntry
import io.verik.importer.ast.sv.element.declaration.SvFunction
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.declaration.SvStruct
import io.verik.importer.ast.sv.element.declaration.SvStructEntry
import io.verik.importer.ast.sv.element.declaration.SvTask
import io.verik.importer.ast.sv.element.declaration.SvTypeAlias
import io.verik.importer.ast.sv.element.declaration.SvValueParameter
import io.verik.importer.ast.sv.element.descriptor.SvBitDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvPackedDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvReferenceDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvSimpleDescriptor
import io.verik.importer.ast.sv.element.expression.SvLiteralExpression
import io.verik.importer.ast.sv.element.expression.SvNothingExpression
import io.verik.importer.ast.sv.element.expression.SvReferenceExpression
import io.verik.importer.message.Messages

class ElementPrinter : SvVisitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitElement(element: SvElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to print element: $element")
    }

    override fun visitCompilationUnit(compilationUnit: SvCompilationUnit) {
        build("CompilationUnit") {
            build(compilationUnit.declarations)
        }
    }

    override fun visitPackage(`package`: SvPackage) {
        build("Package") {
            build(`package`.name)
            build(`package`.declarations)
        }
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitClass(`class`: SvClass) {
        build("Class") {
            build(`class`.name)
            build(`class`.declarations)
            build(`class`.superDescriptor)
        }
    }

    override fun visitModule(module: SvModule) {
        build("Module") {
            build(module.name)
            build(module.declarations)
            build(module.ports)
        }
    }

    override fun visitStruct(struct: SvStruct) {
        build("Struct") {
            build(struct.name)
            build(struct.entries)
        }
    }

    override fun visitEnum(enum: SvEnum) {
        build("Enum") {
            build(enum.name)
            build(enum.entries)
        }
    }

    override fun visitTypeAlias(typeAlias: SvTypeAlias) {
        build("TypeAlias") {
            build(typeAlias.name)
            build(typeAlias.descriptor)
        }
    }

// Function Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitFunction(function: SvFunction) {
        build("Function") {
            build(function.name)
            build(function.valueParameters)
            build(function.descriptor)
        }
    }

    override fun visitTask(task: SvTask) {
        build("Task") {
            build(task.name)
            build(task.valueParameters)
        }
    }

    override fun visitConstructor(constructor: SvConstructor) {
        build("Constructor") {
            build(constructor.valueParameters)
        }
    }

// Property Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitProperty(property: SvProperty) {
        build("Property") {
            build(property.name)
            build(property.descriptor)
        }
    }

    override fun visitValueParameter(valueParameter: SvValueParameter) {
        build("ValueParameter") {
            build(valueParameter.name)
            build(valueParameter.descriptor)
        }
    }

    override fun visitPort(port: SvPort) {
        build("Port") {
            build(port.name)
            build(port.descriptor)
            build(port.portType.toString())
        }
    }

    override fun visitStructEntry(structEntry: SvStructEntry) {
        build("StructEntry") {
            build(structEntry.name)
            build(structEntry.descriptor)
        }
    }

    override fun visitEnumEntry(enumEntry: SvEnumEntry) {
        build("EnumEntry") {
            build(enumEntry.name)
        }
    }

// Descriptor Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitSimpleDescriptor(simpleDescriptor: SvSimpleDescriptor) {
        build("SimpleDescriptor") {
            build(simpleDescriptor.type.toString())
        }
    }

    override fun visitBitDescriptor(bitDescriptor: SvBitDescriptor) {
        build("BitDescriptor") {
            build(bitDescriptor.type.toString())
            build(bitDescriptor.left)
            build(bitDescriptor.right)
            build(bitDescriptor.isSigned)
        }
    }

    override fun visitPackedDescriptor(packedDescriptor: SvPackedDescriptor) {
        build("PackedDescriptor") {
            build(packedDescriptor.type.toString())
            build(packedDescriptor.descriptor)
            build(packedDescriptor.left)
            build(packedDescriptor.right)
        }
    }

    override fun visitReferenceDescriptor(referenceDescriptor: SvReferenceDescriptor) {
        build("ReferenceDescriptor") {
            build(referenceDescriptor.type.toString())
            build(referenceDescriptor.name)
        }
    }

// Expression Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitNothingExpression(nothingExpression: SvNothingExpression) {
        build("NothingExpression") {}
    }

    override fun visitLiteralExpression(literalExpression: SvLiteralExpression) {
        build("LiteralExpression") {
            build(literalExpression.value)
        }
    }

    override fun visitReferenceExpression(referenceExpression: SvReferenceExpression) {
        build("ReferenceExpression") {
            build(referenceExpression.name)
            build(referenceExpression.reference.name)
        }
    }

    private fun build(content: Boolean) {
        if (!first) builder.append(", ")
        builder.append(if (content) "1" else "0")
        first = false
    }

    private fun build(content: String?) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: SvElement?) {
        if (element != null) {
            element.accept(this)
        } else {
            if (!first) builder.append(", ")
            builder.append("null")
            first = false
        }
    }

    private fun build(name: String, content: () -> Unit) {
        if (!first) builder.append(", ")
        builder.append("$name(")
        first = true
        content()
        builder.append(")")
        first = false
    }

    private fun build(elements: List<SvElement>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { it.accept(this) }
        builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: SvElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}
