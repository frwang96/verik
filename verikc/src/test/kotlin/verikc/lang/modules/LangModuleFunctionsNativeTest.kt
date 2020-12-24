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
import verikc.sv.SvUtil

internal class LangModuleFunctionsNativeTest {

    @Test
    fun `function native not bool`() {
        val moduleContext = """
            val a = _bool()
        """.trimIndent()
        val string = """
            !a
        """.trimIndent()
        val expected = """
            !a;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native add int int`() {
        val string = """
            1 + 1
        """.trimIndent()
        val expected = """
            1 + 1;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", "", string))
    }

    @Test
    fun `function native add ubit ubit`() {
        val moduleContext = """
            val x = _ubit(8)
            val y = _ubit(8)
        """.trimIndent()
        val string = """
            x + y
        """.trimIndent()
        val expected = """
            x + y;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native equality instance instance`() {
        val moduleContext = """
            val x = _ubit(8)
            val y = _ubit(8)
        """.trimIndent()
        val string = """
            x == y
        """.trimIndent()
        val expected = """
            x == y;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native equality instance instance width inference`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x == ubit(0)
        """.trimIndent()
        val expected = """
            x == 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }
}
