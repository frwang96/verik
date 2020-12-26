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

internal class LangModuleControlTest {

    @Test
    fun `function delay int`() {
        LangModuleUtil.check(
            "",
            "",
            "delay(1)",
            "#1;"
        )
    }

    @Test
    fun `function wait event`() {
        LangModuleUtil.check(
            "",
            "",
            "wait(posedge(false))",
            "@(posedge 1'b0);"
        )
    }

    @Test
    fun `function posedge bool`() {
        LangModuleUtil.check(
            "",
            "val a = _bool()",
            "posedge(a)",
            "posedge a;"
        )
    }

    @Test
    fun `function negedge bool`() {
        LangModuleUtil.check(
            "",
            "val a = _bool()",
            "negedge(a)",
            "negedge a;"
        )
    }
}
