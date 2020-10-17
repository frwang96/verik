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

package verik.core.sv

import org.junit.jupiter.api.Test
import verik.core.assertStringEquals
import verik.core.sv.build.SvSourceBuilder

internal class SvEnumTest {

    @Test
    fun `enum simple`() {
        val enum = SvEnum(
                0,
                "op",
                listOf(
                        SvEnumEntry(0, "OP_ADD", SvExpressionLiteral(0, "1'h0")),
                        SvEnumEntry(0, "OP_SUB", SvExpressionLiteral(0, "1'h1"))
                ),
                1
        )
        val expected = """
            typedef enum logic [0:0] {
              OP_ADD = 1'h0,
              OP_SUB = 1'h1
            } op;
        """.trimIndent()
        val builder = SvSourceBuilder()
        enum.build(builder)
        assertStringEquals(expected, builder)
    }
}