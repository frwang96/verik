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

internal class LangModuleControlTest {

    @Test
    fun `function delay`() {
        val string = """
            delay(1)
        """.trimIndent()
        val expected = """
            #1;
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression("", string))
    }

    @Test
    fun `function posedge`() {
        val context = """
            val a = _bool()
        """.trimIndent()
        val string = """
            posedge(a)
        """.trimIndent()
        val expected = """
            posedge a;
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression(context, string))
    }

    @Test
    fun `function negedge`() {
        val context = """
            val a = _bool()
        """.trimIndent()
        val string = """
            negedge(a)
        """.trimIndent()
        val expected = """
            negedge a;
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression(context, string))
    }

    @Test
    fun `operator if`() {
        val context = """
            val a = _bool()
        """.trimIndent()
        val string = """
            if (a) {}
        """.trimIndent()
        val expected = """
            if (a) begin
            end
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression(context, string))
    }

    @Test
    fun `operator if else`() {
        val context = """
            val a = _bool()
        """.trimIndent()
        val string = """
            if (a) {} else {}
        """.trimIndent()
        val expected = """
            if (a) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpression(context, string))
    }
}
