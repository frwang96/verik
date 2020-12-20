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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.KtUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.TYPE_INT

internal class KtParserFileTest {

    @Test
    fun `file simple`() {
        val string = "package base"
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(Symbol(2), listOf(), listOf())
        assertEquals(expected, file)
    }

    @Test
    fun `package mismatch`() {
        val string = "package pkg"
        assertThrowsMessage<LineException>("package header does not match file path") {
            KtUtil.resolveFile(string)
        }
    }

    @Test
    fun `import all`() {
        val string = """
            package base
            import y.*
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(Symbol(2), listOf(KtImportEntryAll(Line(2), "y", null)), listOf())
        assertEquals(expected, file)
    }

    @Test
    fun `import identifier`() {
        val string = """
            package base
            import y.z
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(Symbol(2), listOf(KtImportEntryIdentifier(Line(2), "y", null, "z")), listOf())
        assertEquals(expected, file)
    }

    @Test
    fun `declaration simple`() {
        val string = """
            package base
            val x = 0
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(
            Symbol(2),
            listOf(),
            listOf(
                KtPrimaryProperty(
                    Line(2),
                    "x",
                    Symbol(3),
                    TYPE_INT,
                    listOf(),
                    KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))
                )
            )
        )
        assertEquals(expected, file)
    }
}
