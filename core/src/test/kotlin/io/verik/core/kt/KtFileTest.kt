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

import io.verik.core.al.AlRuleParser
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtFileTest {

    @Test
    fun `file package header`() {
        val rule = AlRuleParser.parseKotlinFile("package x")
        val file = KtFile(rule)
        val expected = KtFile(
                "x",
                listOf(),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file import all`() {
        val rule = AlRuleParser.parseKotlinFile("import x.*")
        val file = KtFile(rule)
        val expected = KtFile(
                "",
                listOf(KtImportEntryAll(1, "x")),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file import identifier`() {
        val rule = AlRuleParser.parseKotlinFile("import x.y")
        val file = KtFile(rule)
        val expected = KtFile(
                "",
                listOf(KtImportEntryIdentifier(1, "x", "y")),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file declaration`() {
        val rule = AlRuleParser.parseKotlinFile("val x = 0")
        val file = KtFile(rule)
        val expected = KtFile(
                "",
                listOf(),
                listOf(KtDeclarationProperty(
                        1,
                        "x",
                        Symbol(0),
                        listOf(),
                        KtExpressionLiteral(1, "0")
                ))
        )
        assertEquals(expected, file)
    }
}