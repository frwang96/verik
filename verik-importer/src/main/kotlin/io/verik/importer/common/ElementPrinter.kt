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

import io.verik.importer.ast.sv.element.SvClass
import io.verik.importer.ast.sv.element.SvCompilationUnit
import io.verik.importer.ast.sv.element.SvElement
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPackage
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty
import io.verik.importer.ast.sv.element.SvSimpleTypeDescriptor

class ElementPrinter : SvVisitor() {

    private val builder = StringBuilder()
    private var first = true

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
        }
    }

    override fun visitModule(module: SvModule) {
        build("Module") {
            build(module.name)
            build(module.ports)
        }
    }

    override fun visitProperty(property: SvProperty) {
        build("Property") {
            build(property.name)
            build(property.type.toString())
            build(property.typeDescriptor)
        }
    }

    override fun visitPort(port: SvPort) {
        build("Port") {
            build(port.name)
            build(port.type.toString())
            build(port.typeDescriptor)
            build(port.portType.toString())
        }
    }

    override fun visitSimpleTypeDescriptor(simpleTypeDescriptor: SvSimpleTypeDescriptor) {
        build("SimpleTypeDescriptor") {
            build(simpleTypeDescriptor.type.toString())
        }
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
