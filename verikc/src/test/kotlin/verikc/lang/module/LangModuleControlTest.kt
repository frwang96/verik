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

internal class LangModuleControlTest {

    @Test
    fun `function delay int`() {
        LangUtil.check(
            "",
            "",
            "delay(1)",
            "#1;"
        )
    }

    @Test
    fun `function wait event`() {
        LangUtil.check(
            "",
            "",
            "wait(posedge(false))",
            "@(posedge 1'b0);"
        )
    }

    @Test
    fun `function wait clockport`() {
        LangUtil.check(
            """
                class CP: ClockPort()
            """.trimIndent(),
            """
                @make val cp = CP() with {
                    on (posedge(false)) {}
                }
            """.trimIndent(),
            "wait(cp)",
            "@(cp);"
        )
    }

    @Test
    fun `function posedge boolean`() {
        LangUtil.check(
            "",
            "val a = t_Boolean()",
            "posedge(a)",
            "posedge a;"
        )
    }

    @Test
    fun `function negedge boolean`() {
        LangUtil.check(
            "",
            "val a = t_Boolean()",
            "negedge(a)",
            "negedge a;"
        )
    }

    @Test
    fun `operator forever`() {
        LangUtil.check(
            "",
            "",
            "forever { 0 }",
            """
                forever begin
                    0;
                end
            """.trimIndent()
        )
    }

    @Test
    fun `operator repeat`() {
        LangUtil.check(
            "",
            "",
            "repeat (1) { 0 }",
            """
                repeat (1) begin
                    0;
                end
            """.trimIndent()
        )
    }
}
