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

import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProject
import io.verik.importer.ast.element.EProperty
import io.verik.importer.ast.element.ERootPackage

class ElementPrinter : Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitProject(compilationUnit: EProject) {
        build("Project") {
            build(compilationUnit.rootPackage)
        }
    }

    override fun visitRootPackage(rootPackage: ERootPackage) {
        build("RootPackage") {
            build(rootPackage.declarations)
        }
    }

    override fun visitModule(module: EModule) {
        build("Module") {
            build(module.name)
            build(module.ports)
        }
    }

    override fun visitProperty(property: EProperty) {
        build("Property") {
            build(property.name)
            build(property.type.toString())
        }
    }

    override fun visitPort(port: EPort) {
        build("Port") {
            build(port.name)
            build(port.type.toString())
            build(port.portType.toString())
        }
    }

    private fun build(content: String) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: EElement) {
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

    private fun build(elements: List<EElement>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { it.accept(this) }
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
