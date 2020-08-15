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

package verik.core.kt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.main.LineException
import verik.core.symbol.Symbol

internal class KtFileTest {

    @Test
    fun `file simple`() {
        val rule = AlRuleParser.parseKotlinFile("package x")
        val file = KtUtil.resolveFile(rule)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `package mismatch`() {
        val rule = AlRuleParser.parseKotlinFile("package y")
        assertThrowsMessage<LineException>("package header does not match file path") {
            KtUtil.resolveFile(rule)
        }
    }

    @Test
    fun `import all`() {
        val rule = AlRuleParser.parseKotlinFile("""
            package x
            import y.*
        """.trimIndent())
        val file = KtUtil.resolveFile(rule)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(KtImportEntryAll(2, "y", null)),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `import identifier`() {
        val rule = AlRuleParser.parseKotlinFile("""
            package x
            import y.z
        """.trimIndent())
        val file = KtUtil.resolveFile(rule)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(KtImportEntryIdentifier(2, "y", null, "z")),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `declaration simple`() {
        val rule = AlRuleParser.parseKotlinFile("""
            package x
            val x = 0
        """.trimIndent())
        val file = KtUtil.resolveFile(rule)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf(KtDeclarationBaseProperty(
                        2,
                        "x",
                        Symbol(1, 1, 1),
                        TYPE_INT,
                        listOf(),
                        KtExpressionLiteral(2, TYPE_INT, "0")
                ))
        )
        assertEquals(expected, file)
    }
}