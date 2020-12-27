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

package verikc.sv.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.SvPrimaryProperty
import verikc.sv.ast.SvTypeExtracted

internal class SvPrimaryPropertyExtractorTest {

    @Test
    fun `primary property bool`() {
        val string = """
            val x = _bool()
        """.trimIndent()
        val expected = SvPrimaryProperty(
            line(4),
            "x",
            SvTypeExtracted("logic", "", "")
        )
        assertEquals(expected, SvExtractUtil.extractPrimaryProperty("", string))
    }

    @Test
    fun `primary property ubit`() {
        val string = """
            val x = _ubit(8)
        """.trimIndent()
        val expected = SvPrimaryProperty(
            line(4),
            "x",
            SvTypeExtracted("logic", "[7:0]", "")
        )
        assertEquals(expected, SvExtractUtil.extractPrimaryProperty("", string))
    }

    @Test
    fun `primary property enum`() {
        val fileContext = """
            enum class _op(override val value: _ubit = enum_sequential()): _enum {
                ADD, SUB
            }
        """.trimIndent()
        val string = """
            val op = _op()
        """.trimIndent()
        val expected = SvPrimaryProperty(
            line(6),
            "op",
            SvTypeExtracted("test_pkg::op", "", "")
        )
        assertEquals(expected, SvExtractUtil.extractPrimaryProperty(fileContext, string))
    }
}
