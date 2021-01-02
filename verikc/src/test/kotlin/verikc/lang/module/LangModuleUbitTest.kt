/*
 * Copyright (c) 2021 Francis Wang
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

internal class LangModuleUbitTest {

    @Test
    fun `function ubit int illegal`() {
        LangUtil.checkThrows(
            "",
            "",
            "ubit(0)",
            "could not infer width of ubit"
        )
    }

    @Test
    fun `function ubit int int`() {
        LangUtil.check(
            "",
            "",
            "ubit(8, 0)",
            "8'h00;"
        )
    }

    @Test
    fun `function native gt ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x > ubit(0)",
            "x > 8'h00;"
        )
    }

    @Test
    fun `function native geq ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x >= ubit(0)",
            "x >= 8'h00;"
        )
    }

    @Test
    fun `function native lt ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x < ubit(0)",
            "x < 8'h00;"
        )
    }

    @Test
    fun `function native leq ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x <= ubit(0)",
            "x <= 8'h00;"
        )
    }

    @Test
    fun `function native get ubit int`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x[0]",
            "x[0];"
        )
    }

    @Test
    fun `function native get ubit int int`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x[3, 0] + ubit(0)",
            "x[3:0] + 4'h0;"
        )
    }

    @Test
    fun `function native add ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "(ubit(0) + x) + ubit(0)",
            "8'h00 + x + 8'h00;"
        )
    }

    @Test
    fun `function sl ubit int`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x sl 4",
            "x << 4;"
        )
    }

    @Test
    fun `function sr ubit int`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x sr 4",
            "x >> 4;"
        )
    }

    @Test
    fun `function add ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "(ubit(0) add x) + ubit(0)",
            "8'h00 + x + 9'h000;",
        )
    }

    @Test
    fun `function mul ubit ubit`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "(ubit(0) mul x) + ubit(0)",
            "8'h00 * x + 16'h0000;"
        )
    }
}