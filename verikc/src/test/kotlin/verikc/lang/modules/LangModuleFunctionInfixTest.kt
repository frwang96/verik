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

package verikc.lang.modules

import org.junit.jupiter.api.Test

internal class LangModuleFunctionInfixTest {

    @Test
    fun `function add ubit ubit`() {
        LangModuleUtil.check(
            "",
            "val x = _ubit(8)",
            "(ubit(0) add x) + ubit(0)",
            "8'h00 + x + 9'h000;",
        )
    }

    @Test
    fun `function mul ubit ubit`() {
        LangModuleUtil.check(
            "",
            "val x = _ubit(8)",
            "(ubit(0) mul x) + ubit(0)",
            "8'h00 * x + 16'h0000;"
        )
    }

    @Test
    fun `function sl ubit int`() {
        LangModuleUtil.check(
            "",
            "val x = _ubit(8)",
            "x sl 4",
            "x << 4;"
        )
    }

    @Test
    fun `function sr ubit int`() {
        LangModuleUtil.check(
            "",
            "val x = _ubit(8)",
            "x sr 4",
            "x >> 4;"
        )
    }
}