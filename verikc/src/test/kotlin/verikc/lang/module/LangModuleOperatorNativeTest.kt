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

package verikc.lang.module

import org.junit.jupiter.api.Test

internal class LangModuleOperatorNativeTest {

    @Test
    fun `operator if`() {
        LangModuleUtil.check(
            "",
            "val a = _bool()",
            "if (a) {}",
            """
                if (a) begin
                end
            """.trimIndent()
        )
    }

    @Test
    fun `operator if else`() {
        LangModuleUtil.check(
            "",
            "val a = _bool()",
            "if (a) {} else {}",
            """
                if (a) begin
                end
                else begin
                end
            """.trimIndent()
        )
    }
}
