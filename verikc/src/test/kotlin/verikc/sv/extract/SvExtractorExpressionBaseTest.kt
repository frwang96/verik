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

package verikc.sv.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.*

internal class SvExtractorExpressionBaseTest {

    @Test
    fun `function finish`() {
        val string = "finish()"
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$finish",
            listOf()
        )
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `function simple`() {
        val componentContext = """
            fun g() {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()
        val expected = SvExpressionFunction(
            line(6),
            null,
            "g",
            listOf()
        )
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression("", componentContext, string)
        )
    }

    @Test
    fun `function instance constructor`() {
        val fileContext = """
            class _c: _class()
        """.trimIndent()
        val componentContext = """
            val c = _c()
        """.trimIndent()
        val string = """
            c = c()
        """.trimIndent()
        val expected = SvExpressionFunction(
            line(6),
            null,
            "new",
            listOf()
        )
        val expression = SvExtractUtil.extractComponentActionBlockExpression(fileContext, componentContext, string)
        assertEquals(
            expected,
            (expression as SvExpressionOperator).args[0]
        )
    }

    @Test
    fun `operator forever`() {
        val string = "forever {}"
        val expected = SvExpressionControlBlock(
            line(6),
            SvControlBlockType.FOREVER,
            null,
            listOf(),
            listOf(SvBlock(line(6), listOf(), listOf(), listOf()))
        )
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `property bool`() {
        val componentContext = """
            val x = _bool()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        val expected = SvExpressionProperty(line(6), null, "x")
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression("", componentContext, string)
        )
    }

    @Test
    fun `property bus`() {
        val fileContext = """
            class _b: _bus() {
                val x = _bool()
            }
        """.trimIndent()
        val componentContext = """
            @bus val b = _b() with {}
        """.trimIndent()
        val string = """
            b.x
        """.trimIndent()
        val expected = SvExpressionProperty(line(8), SvExpressionProperty(line(8), null, "b"), "x")
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression(fileContext, componentContext, string)
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
            _op.ADD
        """.trimIndent()
        val expected = SvExpressionProperty(
            line(8),
            null,
            "test_pkg::OP_ADD"
        )
        assertEquals(
            expected,
            SvExtractUtil.extractComponentActionBlockExpression(fileContext, "", string)
        )
    }
}
