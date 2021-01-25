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

internal class LangModuleStringTest {

    @Test
    fun `function native string expression boolean`() {
        LangUtil.check(
            "",
            "",
            "\"\${false}\"",
            "\$sformatf(\"%b\", 1'b0);"
        )
    }

    @Test
    fun `function native string expression int`() {
        LangUtil.check(
            "",
            "",
            "\"\${0}\"",
            "\$sformatf(\"%0d\", 0);"
        )
    }

    @Test
    fun `function native string expression hex`() {
        LangUtil.check(
            "",
            "",
            "\"0x\${0}\"",
            "\$sformatf(\"0x%h\", 0);"
        )
    }

    @Test
    fun `function native string expression time`() {
        LangUtil.check(
            "",
            "",
            "\"\${time()}\"",
            "\$sformatf(\"%0t\", \$time());"
        )
    }

    @Test
    fun `function print string`() {
        LangUtil.check(
            "",
            "",
            "print(\"0\")",
            "\$write(\"0\");"
        )
    }

    @Test
    fun `function print int`() {
        LangUtil.check(
            "",
            "",
            "print(0)",
            "\$write(\"%0d\", 0);"
        )
    }

    @Test
    fun `function println`() {
        LangUtil.check(
            "",
            "",
            "println()",
            "\$display();"
        )
    }

    @Test
    fun `function println string`() {
        LangUtil.check(
            "",
            "",
            "println(\"0\")",
            "\$display(\"0\");"
        )
    }

    @Test
    fun `function println boolean`() {
        LangUtil.check(
            "",
            "val a = t_Boolean()",
            "println(a)",
            "\$display(\"%b\", a);"
        )
    }
}
