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

internal class LangModuleSbitTest {

    @Test
    fun `function sbit int illegal`() {
        LangUtil.checkThrows(
            "",
            "",
            "sbit(0)",
            "could not infer width of sbit"
        )
    }

    @Test
    fun `function sbit int int`() {
        LangUtil.check(
            "",
            "",
            "sbit(8, 0)",
            "8'sh00;"
        )
    }

    @Test
    fun `function native gt sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x > sbit(0)",
            "x > 8'sh00;"
        )
    }

    @Test
    fun `function native geq sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x >= sbit(0)",
            "x >= 8'sh00;"
        )
    }

    @Test
    fun `function native lt sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x < sbit(0)",
            "x < 8'sh00;"
        )
    }

    @Test
    fun `function native leq sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x <= sbit(0)",
            "x <= 8'sh00;"
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

    @Test
    fun `function native not sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "!x",
            "!x;"
        )
    }

    @Test
    fun `function sl sbit int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x sl 4",
            "x <<< 4;"
        )
    }

    @Test
    fun `function sr sbit int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x sr 4",
            "x >>> 4;"
        )
    }

    @Test
    fun `function and sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x and ubit(0)",
            "x & 8'h00;"
        )
    }

    @Test
    fun `function and sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x and sbit(0)",
            "x & 8'sh00;"
        )
    }

    @Test
    fun `function or sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x or ubit(0)",
            "x | 8'h00;"
        )
    }

    @Test
    fun `function or sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x or sbit(0)",
            "x | 8'sh00;"
        )
    }

    @Test
    fun `function xor sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x xor ubit(0)",
            "x ^ 8'h00;"
        )
    }

    @Test
    fun `function xor sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x xor sbit(0)",
            "x ^ 8'sh00;"
        )
    }

    @Test
    fun `function inv sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.inv()",
            "~x;"
        )
    }

    @Test
    fun `function red and sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.red_and()",
            "&x;"
        )
    }

    @Test
    fun `function red or sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.red_or()",
            "|x;"
        )
    }

    @Test
    fun `function red xor sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.red_xor()",
            "^x;"
        )
    }

    @Test
    fun `function ext sbit int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.ext(16)",
            "16'(x);"
        )
    }

    @Test
    fun `function tru sbit int`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "x.tru(4)",
            "4'(x);"
        )
    }
}