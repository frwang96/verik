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

package verikc.lang.module

import org.junit.jupiter.api.Test
import verikc.lang.LangUtil

internal class LangModuleSpecialTest {

    @Test
    fun `operator return unit`() {
        LangUtil.check(
            "",
            "",
            "return",
            "return;"
        )
    }

    @Test
    fun `operator return`() {
        LangUtil.check(
            "",
            "",
            "return 0",
            "return 0;"
        )
    }

    @Test
    fun `operator if`() {
        LangUtil.check(
            "",
            "val a = t_Boolean()",
            "if (a) {}",
            """
                if (a) begin
                end
            """.trimIndent()
        )
    }

    @Test
    fun `operator if else`() {
        LangUtil.check(
            "",
            "val a = t_Boolean()",
            "if (a) {} else {}",
            """
                if (a) begin
                end
                else begin
                end
            """.trimIndent()
        )
    }

    @Test
    fun `operator if else expression`() {
        LangUtil.check(
            "",
            """
                val x = t_Ubit(8)
                val y = t_Ubit(8)
            """.trimIndent(),
            "x = if (true) y else u(0)",
            "x = 1'b1 ? y : 8'h00;"
        )
    }

    @Test
    fun `operator if else infer width`() {
        LangUtil.check(
            "",
            """
                val x = t_Ubit(8)
            """.trimIndent(),
            "x = if (true) u(1) else u(0)",
            "x = 1'b1 ? 8'h01 : 8'h00;"
        )
    }

    @Test
    fun `property this`() {
        LangUtil.check(
            "",
            "val x = t_Int()",
            "this.x",
            "this.x;"
        )
    }
}
