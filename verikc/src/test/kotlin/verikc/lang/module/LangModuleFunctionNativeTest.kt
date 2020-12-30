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

internal class LangModuleFunctionNativeTest {

    @Test
    fun `function native not bool`() {
        LangUtil.check(
            "",
            "val a = _bool()",
            "!a",
            "!a;"
        )
    }

    @Test
    fun `function native add int int`() {
        LangUtil.check(
            "",
            "",
            "1 + 1",
            "1 + 1;"
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
    fun `function native add sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(sbit(0) + x) + sbit(0)",
            "8'sh00 + x + 8'sh00;"
        )
    }

    @Test
    fun `function native sub int int`() {
        LangUtil.check(
            "",
            "",
            "1 - 1",
            "1 - 1;"
        )
    }

    @Test
    fun `function native mul int int`() {
        LangUtil.check(
            "",
            "",
            "1 * 1",
            "1 * 1;"
        )
    }

    @Test
    fun `function native eq instance instance`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x == ubit(0)",
            "x == 8'h00;"
        )
    }

    @Test
    fun `function native neq instance instance`() {
        LangUtil.check(
            "",
            "val x = _ubit(8)",
            "x != ubit(0)",
            "x != 8'h00;"
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
    fun `function native get sbit int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x[0]",
            "x[0];"
        )
    }

    @Test
    fun `function native get sbit int int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x[3, 0] + sbit(0)",
            "x[3:0] + 4'sh0;"
        )
    }
}
