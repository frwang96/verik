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
import verikc.sv.ast.*

internal class SvExpressionExtractorTest {

    @Test
    fun `function finish`() {
        val string = "finish()"
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$finish",
            listOf()
        )
        assertEquals(expected, SvExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `operator forever`() {
        val string = "forever {}"
        val expected = SvExpressionControlBlock(
            line(6),
            SvControlBlockType.FOREVER,
            listOf(),
            listOf(SvBlock(line(6), listOf()))
        )
        assertEquals(expected, SvExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `property bool`() {
        val moduleContext = """
            val x = _bool()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        val expected = SvExpressionProperty(line(6), null, "x")
        assertEquals(expected, SvExtractUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `property enum`() {
        val fileContext = """
            enum class _op(override val value: _ubit = enum_sequential()): _enum {
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
        assertEquals(expected, SvExtractUtil.extractExpression(fileContext, "", string))
    }
}
