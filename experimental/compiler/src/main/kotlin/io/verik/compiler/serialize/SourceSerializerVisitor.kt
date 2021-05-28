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
import io.verik.compiler.ast.element.VkBasicClass
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.ast.element.VkModule
import io.verik.compiler.main.messageCollector

class SourceSerializerVisitor(val sourceBuilder: SourceBuilder): Visitor<Unit>() {

    override fun visitFile(file: VkFile) {
        file.declarations.forEach { it.accept(this) }
    }

    override fun visitModule(module: VkModule) {
        sourceBuilder.appendLine("module: ${module.name};", module)
        sourceBuilder.appendLine("endmodule: ${module.name}", module)
    }

    override fun visitBasicClass(basicClass: VkBasicClass) {
        sourceBuilder.appendLine("class: ${basicClass.name};", basicClass)
        sourceBuilder.appendLine("endclass: ${basicClass.name}", basicClass)
    }

    override fun visitDeclaration(declaration: VkDeclaration) {
        messageCollector.error("unable to serialize declaration ${declaration.name}", declaration)
    }
}