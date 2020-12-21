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

package verikc.ps.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.line
import verikc.ps.PsUtil
import verikc.sv.ast.SvPrimaryProperty
import verikc.sv.ast.SvTypeExtracted

internal class PsPrimaryPropertyTest {

    @Test
    fun `extract bool`() {
        val string = """
            val x = _bool()
        """.trimIndent()
        val expected = SvPrimaryProperty(
            line(4),
            SvTypeExtracted("logic", "", ""),
            "x"
        )
        assertEquals(expected, PsUtil.extractPrimaryProperty("", string))
    }

    @Test
    fun `extract ubit`() {
        val string = """
            val x = _ubit(8)
        """.trimIndent()
        val expected = SvPrimaryProperty(
            line(4),
            SvTypeExtracted("logic", "[7:0]", ""),
            "x"
        )
        assertEquals(expected, PsUtil.extractPrimaryProperty("", string))
    }

    @Test
    fun `extract enum`() {
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
            SvTypeExtracted("test_pkg::op", "", ""),
            "op"
        )
        assertEquals(expected, PsUtil.extractPrimaryProperty(fileContext, string))
    }
}
