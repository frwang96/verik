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

package io.verik.compiler.util

import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.ast.element.*

class ElementPrinter: Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitFile(file: VkFile) {
        build("File") {
            build(file.declarations)
        }
    }

    override fun visitBaseClass(baseClass: VkBaseClass) {
        build("BaseClass") {
            build(baseClass.name.toString())
            build(baseClass.typeParameters)
            build(baseClass.declarations)
        }
    }

    override fun visitModule(module: VkModule) {
        build("Module") {
            build(module.name.toString())
        }
    }

    override fun visitBaseFunction(baseFunction: VkBaseFunction) {
        build("BaseFunction") {
            build(baseFunction.name.toString())
            build(baseFunction.type.toString())
            build(baseFunction.annotationType.toString())
            build(baseFunction.bodyBlockExpression)
        }
    }

    override fun visitBaseProperty(baseProperty: VkBaseProperty) {
        build("BaseProperty") {
            build(baseProperty.name.toString())
            build(baseProperty.type.toString())
            build(baseProperty.initializer)
        }
    }

    override fun visitTypeParameter(typeParameter: VkTypeParameter) {
        build("TypeParameter") {
            build(typeParameter.name.toString())
            build(typeParameter.type.toString())
        }
    }

    override fun visitBlockExpression(blockExpression: VkBlockExpression) {
        build("BlockExpression") {
            build(blockExpression.statements)
        }
    }

    override fun visitReferenceExpression(referenceExpression: VkReferenceExpression) {
        build("ReferenceExpression") {
            build(referenceExpression.name.toString())
        }
    }

    override fun visitConstantExpression(constantExpression: VkConstantExpression) {
        build("ConstantExpression") {
            build(constantExpression.kind.toString())
            build(constantExpression.value)
        }
    }

    private fun build(content: String) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: VkElement?) {
        if (element != null)
            element.accept(this)
        else {
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

    private fun build(elements: List<VkElement>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { it.accept(this) }
        builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: VkElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}