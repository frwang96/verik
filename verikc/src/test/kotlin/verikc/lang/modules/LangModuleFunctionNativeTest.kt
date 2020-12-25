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

internal class LangModuleFunctionNativeTest {

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
        """.trimIndent()
        val string = """
            ubit(0) + x
        """.trimIndent()
        val expected = """
            8'h00 + x;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native add sbit sbit`() {
        val moduleContext = """
            val x = _sbit(8)
        """.trimIndent()
        val string = """
            sbit(0) + x
        """.trimIndent()
        val expected = """
            8'sh00 + x;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native eq instance instance`() {
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

    @Test
    fun `function native neq instance instance`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x != ubit(0)
        """.trimIndent()
        val expected = """
            x != 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native gt ubit ubit`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x > ubit(0)
        """.trimIndent()
        val expected = """
            x > 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native geq ubit ubit`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x >= ubit(0)
        """.trimIndent()
        val expected = """
            x >= 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native lt ubit ubit`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x < ubit(0)
        """.trimIndent()
        val expected = """
            x < 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native leq ubit ubit`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x <= ubit(0)
        """.trimIndent()
        val expected = """
            x <= 8'h00;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native get ubit int`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x[0]
        """.trimIndent()
        val expected = """
            x[0];
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native get ubit int int`() {
        val moduleContext = """
            val x = _ubit(8)
        """.trimIndent()
        val string = """
            x[3, 0] + ubit(0)
        """.trimIndent()
        val expected = """
            x[3:0] + 4'h0;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native get sbit int`() {
        val moduleContext = """
            val x = _sbit(8)
        """.trimIndent()
        val string = """
            x[0]
        """.trimIndent()
        val expected = """
            x[0];
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }

    @Test
    fun `function native get sbit int int`() {
        val moduleContext = """
            val x = _sbit(8)
        """.trimIndent()
        val string = """
            x[3, 0] + sbit(0)
        """.trimIndent()
        val expected = """
            x[3:0] + 4'sh0;
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", moduleContext, string))
    }
}
