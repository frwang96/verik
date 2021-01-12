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

internal class LangModuleMiscTest {

    @Test
    fun `function cat ubit`() {
        LangUtil.check(
            "",
            "",
            "cat(ubit(8, 0x00))",
            "{8'h00};"
        )
    }

    @Test
    fun `function cat ubit ubit`() {
        LangUtil.check(
            "",
            "",
            "cat(ubit(8, 0x00), ubit(8, 0xff))",
            "{8'h00, 8'hff};"
        )
    }

    @Test
    fun `function cat ubit bool`() {
        LangUtil.check(
            "",
            "",
            "cat(ubit(8, 0x00), false)",
            "{8'h00, 1'b0};"
        )
    }

    @Test
    fun `function cat ubit illegal`() {
        LangUtil.checkThrows(
            "",
            "",
            "cat(ubit(0x00))",
            "could not infer width of ubit"
        )
    }

    @Test
    fun `function log int`() {
        LangUtil.check(
            "",
            "",
            "log(2)",
            "\$clog2(2);"
        )
    }

    @Test
    fun `function exp int`() {
        LangUtil.check(
            "",
            "",
            "exp(2)",
            "1 << 2;"
        )
    }
}