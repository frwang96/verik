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

package io.verik.compiler.serialize

import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.ast.element.*
import io.verik.compiler.main.m

class SourceSerializerVisitor(private val sourceBuilder: SourceBuilder): Visitor() {

    private var first = true

    override fun visitFile(file: VkFile) {
        file.declarations.forEach { it.accept(this) }
    }

    override fun visitModule(module: VkModule) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("module $module;", module)
        sourceBuilder.indent {
            module.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endmodule: $module", module)
    }

    override fun visitBaseClass(baseClass: VkBaseClass) {
        appendLineIfNotFirst()
        sourceBuilder.appendLine("class $baseClass;", baseClass)
        sourceBuilder.indent {
            baseClass.declarations.forEach { it.accept(this) }
            sourceBuilder.appendLine()
        }
        sourceBuilder.appendLine("endclass: $baseClass", baseClass)
    }

    override fun visitBaseFunction(baseFunction: VkBaseFunction) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(baseFunction)
        sourceBuilder.appendLine("function $typeString $baseFunction();", baseFunction)
        sourceBuilder.appendLine("endfunction: $baseFunction", baseFunction)
    }

    override fun visitBaseProperty(baseProperty: VkBaseProperty) {
        appendLineIfNotFirst()
        val typeString = TypeSerializer.serialize(baseProperty)
        sourceBuilder.appendLine("$typeString $baseProperty;", baseProperty)
    }

    override fun visitDeclaration(declaration: VkDeclaration) {
        m.error("Unable to serialize declaration: $declaration", declaration)
    }

    private fun appendLineIfNotFirst() {
        if (first) {
            first = false
        } else {
            sourceBuilder.appendLine()
        }
    }
}