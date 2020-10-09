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

package verik.core.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtParserFileTest {

    @Test
    fun `file simple`() {
        val string = "package x"
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `package mismatch`() {
        val string = "package y"
        assertThrowsMessage<LineException>("package header does not match file path") {
            KtUtil.resolveFile(string)
        }
    }

    @Test
    fun `import all`() {
        val string = """
            package x
            import y.*
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(KtImportEntryAll(2, "y", null)),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `import identifier`() {
        val string = """
            package x
            import y.z
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(KtImportEntryIdentifier(2, "y", null, "z")),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `declaration simple`() {
        val string = """
            package x
            val x = 0
        """.trimIndent()
        val file = KtUtil.resolveFile(string)
        val expected = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf(KtDeclarationPrimaryProperty(
                        2,
                        "x",
                        Symbol(1, 1, 1),
                        TYPE_INT,
                        listOf(),
                        KtExpressionLiteral(2, TYPE_INT, LiteralValue.fromInt(0))
                ))
        )
        assertEquals(expected, file)
    }
}