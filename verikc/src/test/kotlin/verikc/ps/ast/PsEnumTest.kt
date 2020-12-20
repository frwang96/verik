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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.ps.PsUtil
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvEnumProperty
import verikc.sv.ast.SvExpressionLiteral

internal class PsEnumTest {

    @Test
    @Disabled
    // TODO extract with file in symbolTable
    fun `extract simple`() {
        val string = """
            enum class _op(override val value: _ubit = enum_sequential()): _enum {
                ADD, SUB
            }
        """.trimIndent()
        val expected = SvEnum(
            Line(1),
            "op",
            listOf(
                SvEnumProperty(Line(2), "OP_ADD", SvExpressionLiteral(Line(1), "1'h0")),
                SvEnumProperty(Line(2), "OP_SUB", SvExpressionLiteral(Line(1), "1'h1"))
            ),
            1
        )
        Assertions.assertEquals(expected, PsUtil.extractEnum(string))
    }
}
