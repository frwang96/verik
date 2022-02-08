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

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFile
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtPackage
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.EStructEntry
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.ast.element.declaration.ESvFunction
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.EArrayDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EIndexDimensionDescriptor
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.ENothingDescriptor
import io.verik.importer.ast.element.descriptor.ERangeDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.element.descriptor.ETypeArgument
import io.verik.importer.message.Messages

class ElementPrinter : Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to print element: $element")
    }

    override fun visitProject(project: EProject) {
        build("Project") {
            build(project.declarations)
        }
    }

    override fun visitSvPackage(`package`: ESvPackage) {
        build("SvPackage") {
            build(`package`.name)
            build(`package`.declarations)
        }
    }

    override fun visitKtPackage(`package`: EKtPackage) {
        build("KtPackage") {
            build(`package`.name)
            build(`package`.files)
        }
    }

    override fun visitKtFile(file: EKtFile) {
        build("KtFile") {
            build(file.name)
            build(file.declarations)
        }
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitSvClass(`class`: ESvClass) {
        build("SvClass") {
            build(`class`.name)
            build(`class`.declarations)
            build(`class`.typeParameters)
            build(`class`.superDescriptor)
        }
    }

    override fun visitKtClass(`class`: EKtClass) {
        build("KtClass") {
            build(`class`.name)
            build(`class`.declarations)
            build(`class`.typeParameters)
            build(`class`.valueParameters)
            build(`class`.superDescriptor)
            build(`class`.isOpen)
        }
    }

    override fun visitModule(module: EModule) {
        build("Module") {
            build(module.name)
            build(module.declarations)
            build(module.typeParameters)
            build(module.ports)
        }
    }

    override fun visitStruct(struct: EStruct) {
        build("Struct") {
            build(struct.name)
            build(struct.entries)
        }
    }

    override fun visitEnum(enum: EEnum) {
        build("Enum") {
            build(enum.name)
            build(enum.entries)
        }
    }

    override fun visitTypeAlias(typeAlias: ETypeAlias) {
        build("TypeAlias") {
            build(typeAlias.name)
            build(typeAlias.descriptor)
        }
    }

    override fun visitTypeParameter(typeParameter: ETypeParameter) {
        build("TypeParameter") {
            build(typeParameter.name)
            build(typeParameter.descriptor)
            build(typeParameter.isCardinal)
        }
    }

// Function Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitSvFunction(function: ESvFunction) {
        build("SvFunction") {
            build(function.name)
            build(function.valueParameters)
            build(function.descriptor)
        }
    }

    override fun visitTask(task: ETask) {
        build("Task") {
            build(task.name)
            build(task.valueParameters)
        }
    }

    override fun visitSvConstructor(constructor: ESvConstructor) {
        build("SvConstructor") {
            build(constructor.valueParameters)
        }
    }

    override fun visitKtFunction(function: EKtFunction) {
        build("KtFunction") {
            build(function.name)
            build(function.valueParameters)
            build(function.descriptor)
            build(function.annotationEntries.map { it.name })
            build(function.isOpen)
            build(function.isOverride)
        }
    }

    override fun visitKtConstructor(constructor: EKtConstructor) {
        build("KtConstructor") {
            build(constructor.valueParameters)
        }
    }

// Property Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitProperty(property: EProperty) {
        build("Property") {
            build(property.name)
            build(property.descriptor)
        }
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        build("SvValueParameter") {
            build(valueParameter.name)
            build(valueParameter.descriptor)
            build(valueParameter.hasDefault)
        }
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        build("KtValueParameter") {
            build(valueParameter.name)
            build(valueParameter.descriptor)
            build(valueParameter.annotationEntries.map { it.name })
            build(valueParameter.isMutable)
            build(valueParameter.hasDefault)
        }
    }

    override fun visitPort(port: EPort) {
        build("Port") {
            build(port.name)
            build(port.descriptor)
            build(port.portType.toString())
        }
    }

    override fun visitStructEntry(structEntry: EStructEntry) {
        build("StructEntry") {
            build(structEntry.name)
            build(structEntry.descriptor)
        }
    }

    override fun visitEnumEntry(enumEntry: EEnumEntry) {
        build("EnumEntry") {
            build(enumEntry.name)
        }
    }

// Descriptor Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitNothingDescriptor(nothingDescriptor: ENothingDescriptor) {
        build("NothingDescriptor") {}
    }

    override fun visitSimpleDescriptor(simpleDescriptor: ESimpleDescriptor) {
        build("SimpleDescriptor") {
            build(simpleDescriptor.type.toString())
        }
    }

    override fun visitLiteralDescriptor(literalDescriptor: ELiteralDescriptor) {
        build("LiteralDescriptor") {
            build(literalDescriptor.type.toString())
            build(literalDescriptor.value)
        }
    }

    override fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
        build("BitDescriptor") {
            build(bitDescriptor.type.toString())
            build(bitDescriptor.left)
            build(bitDescriptor.right)
            build(bitDescriptor.isSigned)
        }
    }

    override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
        build("ReferenceDescriptor") {
            build(referenceDescriptor.type.toString())
            build(referenceDescriptor.name)
            build(referenceDescriptor.reference.name)
            build(referenceDescriptor.typeArguments)
        }
    }

    override fun visitArrayDimensionDescriptor(arrayDimensionDescriptor: EArrayDimensionDescriptor) {
        build("ArrayDimensionDescriptor") {
            build(arrayDimensionDescriptor.type.toString())
            build(arrayDimensionDescriptor.descriptor)
            build(arrayDimensionDescriptor.isQueue)
        }
    }

    override fun visitIndexDimensionDescriptor(indexDimensionDescriptor: EIndexDimensionDescriptor) {
        build("IndexDimensionDescriptor") {
            build(indexDimensionDescriptor.type.toString())
            build(indexDimensionDescriptor.descriptor)
            build(indexDimensionDescriptor.index)
        }
    }

    override fun visitRangeDimensionDescriptor(rangeDimensionDescriptor: ERangeDimensionDescriptor) {
        build("RangeDimensionDescriptor") {
            build(rangeDimensionDescriptor.type.toString())
            build(rangeDimensionDescriptor.descriptor)
            build(rangeDimensionDescriptor.left)
            build(rangeDimensionDescriptor.right)
            build(rangeDimensionDescriptor.isPacked)
        }
    }

    override fun visitTypeArgument(typeArgument: ETypeArgument) {
        build("TypeArgument") {
            build(typeArgument.name)
            build(typeArgument.descriptor)
        }
    }

    private fun build(content: Boolean?) {
        if (!first) builder.append(", ")
        when (content) {
            null -> builder.append("null")
            true -> builder.append("1")
            false -> builder.append("0")
        }
        first = false
    }

    private fun build(content: String?) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: EElement?) {
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

    private fun build(elements: List<Any>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach {
            when (it) {
                is EElement -> it.accept(this)
                else -> build(it.toString())
            }
        }
        builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: EElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}
