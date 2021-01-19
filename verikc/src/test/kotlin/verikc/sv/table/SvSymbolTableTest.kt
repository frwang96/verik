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

package verikc.sv.table

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.SvProperty
import verikc.sv.ast.SvTypeExtracted

internal class SvSymbolTableTest {

    @Test
    fun `property bool`() {
        val string = """
            val x = _bool()
        """.trimIndent()
        val expected = SvProperty(
            line(4),
            "x",
            SvTypeExtracted("logic", "", "")
        )
        Assertions.assertEquals(
            expected,
            SvExtractUtil.extractModuleProperty("", string)
        )
    }

    @Test
    fun `property ubit`() {
        val string = """
            val x = _ubit(8)
        """.trimIndent()
        val expected = SvProperty(
            line(4),
            "x",
            SvTypeExtracted("logic", "[7:0]", "")
        )
        Assertions.assertEquals(
            expected,
            SvExtractUtil.extractModuleProperty("", string)
        )
    }

    @Test
    fun `property array ubit`() {
        val string = """
            val x = _array(16, _ubit(8))
        """.trimIndent()
        val expected = SvProperty(
            line(4),
            "x",
            SvTypeExtracted("logic", "[7:0]", "[16]")
        )
        Assertions.assertEquals(
            expected,
            SvExtractUtil.extractModuleProperty("", string)
        )
    }

    @Test
    fun `property enum`() {
        val fileContext = """
            enum class _op(val value: _ubit = enum_sequential()) {
                ADD, SUB
            }
        """.trimIndent()
        val string = """
            val op = _op()
        """.trimIndent()
        val expected = SvProperty(
            line(6),
            "op",
            SvTypeExtracted("test_pkg::op", "", "")
        )
        Assertions.assertEquals(
            expected,
            SvExtractUtil.extractModuleProperty(fileContext, string)
        )
    }

    @Test
    fun `property class`() {
        val fileContext = """
            class _c: _class()
        """.trimIndent()
        val string = """
            val c = _c()
        """.trimIndent()
        val expected = SvProperty(
            line(4),
            "c",
            SvTypeExtracted("test_pkg::c", "", "")
        )
        Assertions.assertEquals(
            expected,
            SvExtractUtil.extractModuleProperty(fileContext, string)
        )
    }
}