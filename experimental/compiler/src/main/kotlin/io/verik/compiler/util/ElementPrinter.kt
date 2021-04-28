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

import io.verik.compiler.ast.VkClass
import io.verik.compiler.ast.VkElement
import io.verik.compiler.ast.VkFile
import io.verik.compiler.ast.VkTreeVisitor

class ElementPrinter: VkTreeVisitor() {

    private val builder = StringBuilder()

    override fun visitFile(file: VkFile) {
        builder.append("File(")
        super.visitFile(file)
        builder.append(")")
    }

    override fun visitClass(clazz: VkClass) {
        builder.append("Class(${clazz.name})")
    }

    companion object {

        fun dump(element: VkElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}