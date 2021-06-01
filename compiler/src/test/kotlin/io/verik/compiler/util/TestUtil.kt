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

import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.common.ElementPrinter
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