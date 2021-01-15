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

internal class LangModuleIntTest {

    @Test
    fun `function native pre increment int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "++x",
            "++x;"
        )
    }

    @Test
    fun `function native pre decrement int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "--x",
            "--x;"
        )
    }

    @Test
    fun `function native post increment int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x++",
            "x++;"
        )
    }

    @Test
    fun `function native post decrement int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x--",
            "x--;"
        )
    }

    @Test
    fun `function native gt int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x > 0",
            "x > 0;"
        )
    }

    @Test
    fun `function native geq int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x >= 0",
            "x >= 0;"
        )
    }

    @Test
    fun `function native lt int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x < 0",
            "x < 0;"
        )
    }

    @Test
    fun `function native leq int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x <= 0",
            "x <= 0;"
        )
    }


    @Test
    fun `function native add int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x + 1",
            "x + 1;"
        )
    }

    @Test
    fun `function native sub int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x - 1",
            "x - 1;"
        )
    }

    @Test
    fun `function native mul int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x * 1",
            "x * 1;"
        )
    }

    @Test
    fun `function native div int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x / 1",
            "x / 1;"
        )
    }

    @Test
    fun `function native rem int int`() {
        LangUtil.check(
            "",
            "val x = _int()",
            "x % 1",
            "x % 1;"
        )
    }
}