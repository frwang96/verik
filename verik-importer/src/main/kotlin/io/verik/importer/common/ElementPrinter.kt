/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.ECompanionObject
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
import io.verik.importer.common.ElementPrinter.Companion.dump
import io.verik.importer.message.Messages

/**
 * Visitor that builds a string representation of an AST for testing and debug. Obtain the string with the [dump]
 * function.
 */
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

    override fun visitSvPackage(pkg: ESvPackage) {
        build("SvPackage") {
            build(pkg.name)
            build(pkg.declarations)
        }
    }

    override fun visitKtPackage(pkg: EKtPackage) {
        build("KtPackage") {
            build(pkg.name)
            build(pkg.files)
        }
    }

    override fun visitKtFile(file: EKtFile) {
        build("KtFile") {
            build(file.name)
            build(file.declarations)
        }
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitSvClass(cls: ESvClass) {
        build("SvClass") {
            build(cls.name)
            build(cls.declarations)
            build(cls.typeParameters)
            build(cls.superDescriptor)
        }
    }

    override fun visitKtClass(cls: EKtClass) {
        build("KtClass") {
            build(cls.name)
            build(cls.declarations)
            build(cls.typeParameters)
            build(cls.valueParameters)
            build(cls.superDescriptor)
            build(cls.isOpen)
        }
    }

    override fun visitCompanionObject(companionObject: ECompanionObject) {
        build("CompanionObject") {
            build(companionObject.declarations)
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
            build(function.isStatic)
        }
    }

    override fun visitTask(task: ETask) {
        build("Task") {
            build(task.name)
            build(task.valueParameters)
            build(task.isStatic)
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
            build(property.isStatic)
            build(property.isMutable)
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
