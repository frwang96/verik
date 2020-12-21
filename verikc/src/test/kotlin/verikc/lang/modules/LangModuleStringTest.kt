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
import verikc.assertStringEquals

internal class LangModuleStringTest {

    @Test
    fun `function print string`() {
        val string = """
            print("0")
        """.trimIndent()
        val expected = """
            ${'$'}write("0");
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression("", string))
    }

    @Test
    fun `function print int`() {
        val string = """
            print(0)
        """.trimIndent()
        val expected = """
            ${'$'}write("%0d", 0);
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression("", string))
    }

    @Test
    fun `function println string`() {
        val string = """
            println("0")
        """.trimIndent()
        val expected = """
            ${'$'}display("0");
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression("", string))
    }

    @Test
    fun `function println bool`() {
        val context = """
            val a = _bool()
        """.trimIndent()
        val string = """
            println(a)
        """.trimIndent()
        val expected = """
            ${'$'}display("%b", a);
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression(context, string))
    }
}
