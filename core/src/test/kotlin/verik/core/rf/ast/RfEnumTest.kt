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

package verik.core.rf.ast

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.rf.RfUtil
import verik.core.sv.ast.SvEnum
import verik.core.sv.ast.SvEnumEntry
import verik.core.sv.ast.SvExpressionLiteral

internal class RfEnumTest {

    @Test
    fun `extract simple`() {
        val string = """
            enum class _op(override val value: _int): _enum {
                ADD(0), SUB(1)
            }
        """.trimIndent()
        val enum = RfUtil.extractEnum(string)
        val expected = SvEnum(
                1,
                "op",
                listOf(
                        SvEnumEntry(2, "OP_ADD", SvExpressionLiteral(2, "1'h0")),
                        SvEnumEntry(2, "OP_SUB", SvExpressionLiteral(2, "1'h1"))
                ),
                1
        )
        Assertions.assertEquals(expected, enum)
    }
}