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

internal class LangModuleStringTest {

    @Test
    fun `function print string`() {
        LangModuleUtil.check(
            "",
            "",
            "print(\"0\")",
            "\$write(\"0\");"
        )
    }

    @Test
    fun `function print int`() {
        LangModuleUtil.check(
            "",
            "",
            "print(0)",
            "\$write(\"%0d\", 0);"
        )
    }

    @Test
    fun `function println string`() {
        LangModuleUtil.check(
            "",
            "",
            "println(\"0\")",
            "\$display(\"0\");"
        )
    }

    @Test
    fun `function println bool`() {
        LangModuleUtil.check(
            "",
            "val a = _bool()",
            "println(a)",
            "\$display(\"%b\", a);"
        )
    }
}
