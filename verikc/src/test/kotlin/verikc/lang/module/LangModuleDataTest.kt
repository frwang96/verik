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

internal class LangModuleDataTest {

    @Test
    fun `function ubit int illegal`() {
        LangModuleUtil.checkThrows(
            "",
            "",
            "ubit(0)",
            "could not infer width of ubit"
        )
    }

    @Test
    fun `function ubit int int`() {
        LangModuleUtil.check(
            "",
            "",
            "ubit(8, 0)",
            "8'h00;"
        )
    }

    @Test
    fun `function sbit int illegal`() {
        LangModuleUtil.checkThrows(
            "",
            "",
            "sbit(0)",
            "could not infer width of sbit"
        )
    }

    @Test
    fun `function sbit int int`() {
        LangModuleUtil.check(
            "",
            "",
            "sbit(8, 0)",
            "8'sh00;"
        )
    }
}