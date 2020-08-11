/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.kt

import io.verik.core.main.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import io.verik.core.main.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtFileTest {

    @Test
    fun `file empty`() {
        val rule = AlRuleParser.parseKotlinFile("")
        val file = parseFile(rule)
        val expected = KtFile(
                Symbol(1, 1),
                listOf(),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file package header`() {
        val rule = AlRuleParser.parseKotlinFile("package x")
        assertThrowsMessage<LineException>("package header does not match file path") {
            parseFile(rule)
        }
    }

    @Test
    fun `file import all`() {
        val rule = AlRuleParser.parseKotlinFile("import x.*")
        val file = parseFile(rule)
        val expected = KtFile(
                Symbol(1, 1),
                listOf(KtImportEntryAll(1, "x", null)),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file import identifier`() {
        val rule = AlRuleParser.parseKotlinFile("import x.y")
        val file = parseFile(rule)
        val expected = KtFile(
                Symbol(1, 1),
                listOf(KtImportEntryIdentifier(1, "x", null, "y")),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file declaration`() {
        val rule = AlRuleParser.parseKotlinFile("val x = 0")
        val file = parseFile(rule)
        val expected = KtFile(
                Symbol(1, 1),
                listOf(),
                listOf(KtDeclarationProperty(
                        1,
                        "x",
                        Symbol(1, 1, 1),
                        listOf(),
                        KtExpressionLiteral(1, "0")
                ))
        )
        assertEquals(expected, file)
    }
}