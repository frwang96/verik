/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.config.FileConfig
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionLiteral
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtImportEntry
import verikc.kt.ast.KtPrimaryProperty
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line
import java.io.File

internal class KtParserFileTest {

    @Test
    fun `file simple`() {
        val string = "package test"
        val file = KtParseUtil.parseFile(string)
        val fileConfig = FileConfig(
            "test/test.kt",
            File("test/test.kt"),
            File("test/test.txt"),
            File("test/test.kt"),
            File("test/test.sv"),
            File("test/test.svh"),
            FILE_SYMBOL,
            PKG_SYMBOL,
            null
        )
        val expected = KtFile(fileConfig, listOf(), null, listOf(), listOf(), listOf())
        assertEquals(expected, file)
    }

    @Test
    fun `package mismatch`() {
        val string = "package pkg"
        assertThrowsMessage<LineException>("package header does not match file path") {
            KtParseUtil.parseFile(string)
        }
    }

    @Test
    fun `import all`() {
        val string = """
            package test
            import y.*
        """.trimIndent()
        val file = KtParseUtil.parseFile(string)
        val expected = listOf(KtImportEntry(line(2), listOf("y", "*")))
        assertEquals(expected, file.importEntries)
    }

    @Test
    fun `import identifier`() {
        val string = """
            package test
            import y.z
        """.trimIndent()
        val file = KtParseUtil.parseFile(string)
        val expected = listOf(KtImportEntry(line(2), listOf("y", "z")))
        assertEquals(expected, file.importEntries)
    }

    @Test
    fun `property simple`() {
        val string = """
            package test
            val x = 0
        """.trimIndent()
        val file = KtParseUtil.parseFile(string)
        val expected = listOf(
            KtPrimaryProperty(
                line(2),
                "x",
                Symbol(3),
                null,
                listOf(),
                KtExpressionLiteral(line(2), TYPE_INT, LiteralValue.fromInt(0))
            )
        )
        assertEquals(expected, file.properties)
    }
}
