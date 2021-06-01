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

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.common.ElementPrinter
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import org.junit.jupiter.api.Assertions.assertEquals

fun assertElementEquals(expected: String, actual: VkElement) {
    val builder = StringBuilder()
    expected.toCharArray().forEach {
        when {
            it.isWhitespace() -> {}
            it == ',' -> builder.append(", ")
            else -> builder.append(it)
        }
    }
    assertEquals(builder.toString(), ElementPrinter.dump(actual))
}

fun assertOutputTextEquals(expected: String, actual: TextFile) {
    val expectedLines = expected.lines()
        .dropLastWhile { it.isEmpty() }
    val actualLines = actual.content.lines()
        .let { it.subList(8, it.size) }
        .let { if (it[0].startsWith("`timescale")) it.drop(2) else it }
        .dropLastWhile { it.isEmpty() }

    assertEquals(expectedLines, actualLines)
}

fun ProjectContext.findDeclaration(nameString: String): VkDeclaration {
    val declarationVisitor = object: TreeVisitor() {
        val declarations = ArrayList<VkDeclaration>()
        override fun visitDeclaration(declaration: VkDeclaration) {
            if (declaration.name == Name(nameString)) declarations.add(declaration)
            super.visitDeclaration(declaration)
        }
    }
    vkFiles.forEach {
        it.accept(declarationVisitor)
    }
    if (declarationVisitor.declarations.size != 1)
        throw IllegalArgumentException("Could not find unique declaration")
    return declarationVisitor.declarations[0]
}