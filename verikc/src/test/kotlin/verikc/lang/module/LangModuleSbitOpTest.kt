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

internal class LangModuleSbitOpTest {

    @Test
    fun `function native add sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x + u(0)) + u(0)",
            "x + 8'h00 + 8'h00;"
        )
    }

    @Test
    fun `function native add sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x + s(0)) + u(0)",
            "x + 8'sh00 + 8'h00;"
        )
    }

    @Test
    fun `function native sub sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x - u(0)) + u(0)",
            "x - 8'h00 + 8'h00;"
        )
    }

    @Test
    fun `function native sub sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x - s(0)) + u(0)",
            "x - 8'sh00 + 8'h00;"
        )
    }

    @Test
    fun `function native mul sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x * u(0)) + u(0)",
            "x * 8'h00 + 8'h00;"
        )
    }

    @Test
    fun `function native mul sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x * s(0)) + u(0)",
            "x * 8'sh00 + 8'h00;"
        )
    }

    @Test
    fun `function add sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x add u(0)) + u(0)",
            "x + 8'h00 + 9'h000;",
        )
    }

    @Test
    fun `function add sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x add s(0)) + u(0)",
            "x + 8'sh00 + 9'h000;",
        )
    }

    @Test
    fun `function sub sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x sub u(0)) + u(0)",
            "x - 8'h00 + 9'h000;",
        )
    }

    @Test
    fun `function sub sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x sub s(0)) + u(0)",
            "x - 8'sh00 + 9'h000;",
        )
    }

    @Test
    fun `function mul sbit ubit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x mul u(0)) + u(0)",
            "x * 8'h00 + 16'h0000;"
        )
    }

    @Test
    fun `function mul sbit sbit`() {
        LangUtil.check(
            "",
            "val x = _sbit(8)",
            "(x mul s(0)) + u(0)",
            "x * 8'sh00 + 16'h0000;"
        )
    }
}