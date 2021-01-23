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
    fun `function native gt ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x > u(0)",
            "x > 8'h00;"
        )
    }

    @Test
    fun `function native geq ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x >= u(0)",
            "x >= 8'h00;"
        )
    }

    @Test
    fun `function native lt ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x < u(0)",
            "x < 8'h00;"
        )
    }

    @Test
    fun `function native leq ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x <= u(0)",
            "x <= 8'h00;"
        )
    }

    @Test
    fun `function native get ubit int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x[0]",
            "x[0];"
        )
    }

    @Test
    fun `function native get ubit int int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x[3, 0] + u(0)",
            "x[3:0] + 4'h0;"
        )
    }

    @Test
    fun `function native not ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "!x",
            "!x;"
        )
    }

    @Test
    fun `function shl ubit int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x shl 4",
            "x << 4;"
        )
    }

    @Test
    fun `function shr ubit int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x shr 4",
            "x >> 4;"
        )
    }

    @Test
    fun `function and ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x and u(0)",
            "x & 8'h00;"
        )
    }

    @Test
    fun `function and ubit ubit width mismatch`() {
        LangUtil.checkThrows(
            "",
            "val x = t_Ubit(8)",
            "x and u(4, 0)",
            "width mismatch expected 8 but got 4"
        )
    }

    @Test
    fun `function and ubit sbit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x and s(0)",
            "x & 8'sh00;"
        )
    }

    @Test
    fun `function or ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x or u(0)",
            "x | 8'h00;"
        )
    }

    @Test
    fun `function or ubit sbit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x or s(0)",
            "x | 8'sh00;"
        )
    }

    @Test
    fun `function xor ubit ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x xor u(0)",
            "x ^ 8'h00;"
        )
    }

    @Test
    fun `function xor ubit sbit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x xor s(0)",
            "x ^ 8'sh00;"
        )
    }

    @Test
    fun `function inv ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.inv()",
            "~x;"
        )
    }

    @Test
    fun `function red and ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.red_and()",
            "&x;"
        )
    }

    @Test
    fun `function red or ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.red_or()",
            "|x;"
        )
    }

    @Test
    fun `function red xor ubit`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.red_xor()",
            "^x;"
        )
    }

    @Test
    fun `function ext ubit int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.ext(16)",
            "16'(x);"
        )
    }

    @Test
    fun `function ext ubit int illegal`() {
        LangUtil.checkThrows(
            "",
            "val x = t_Ubit(8)",
            "x.ext(4)",
            "extended width 4 not longer than original width 8"
        )
    }

    @Test
    fun `function tru ubit int`() {
        LangUtil.check(
            "",
            "val x = t_Ubit(8)",
            "x.tru(4)",
            "4'(x);"
        )
    }

    @Test
    fun `function tru ubit int illegal`() {
        LangUtil.checkThrows(
            "",
            "val x = t_Ubit(8)",
            "x.tru(16)",
            "truncated width 16 not shorter than original width 8"
        )
    }

    @Test
    fun `function u int illegal`() {
        LangUtil.checkThrows(
            "",
            "",
            "u(0)",
            "could not infer width of ubit"
        )
    }

    @Test
    fun `function u int int`() {
        LangUtil.check(
            "",
            "",
            "u(8, 0)",
            "8'h00;"
        )
    }

    @Test
    fun `function u int int property`() {
        LangUtil.check(
            "",
            "val x = t_Int()",
            "u(8, x)",
            "8'(x);"
        )
    }
}