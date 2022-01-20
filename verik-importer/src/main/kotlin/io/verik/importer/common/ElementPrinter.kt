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
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.descriptor.SvBitDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvPackedDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvSimpleDescriptor
import io.verik.importer.ast.sv.element.expression.SvLiteralExpression
import io.verik.importer.ast.sv.element.expression.SvNothingExpression
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

    override fun visitClass(`class`: SvClass) {
        build("Class") {
            build(`class`.name)
            build(`class`.declarations)
        }
    }

    override fun visitModule(module: SvModule) {
        build("Module") {
            build(module.name)
            build(module.declarations)
            build(module.ports)
        }
    }

    override fun visitProperty(property: SvProperty) {
        build("Property") {
            build(property.name)
            build(property.type.toString())
            build(property.descriptor)
        }
    }

    override fun visitPort(port: SvPort) {
        build("Port") {
            build(port.name)
            build(port.type.toString())
            build(port.descriptor)
            build(port.portType.toString())
        }
    }

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

    override fun visitNothingExpression(nothingExpression: SvNothingExpression) {
        build("NothingExpression") {}
    }

    override fun visitLiteralExpression(literalExpression: SvLiteralExpression) {
        build("LiteralExpression") {
            build(literalExpression.value)
        }
    }

    private fun build(content: Boolean) {
        if (!first) builder.append(", ")
        builder.append(if (content) "1" else "0")
        first = false
    }

    private fun build(content: String) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: SvElement) {
        element.accept(this)
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
