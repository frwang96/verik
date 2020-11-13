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
        val string = "delay(1)"
        val expected = "#1;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function posedge`() {
        val string = "posedge(a)"
        val expected = "posedge a;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function negedge`() {
        val string = "negedge(a)"
        val expected = "negedge a;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `operator if`() {
        val string = "if (a) {}"
        val expected = """
            if (a) begin
            end
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `operator if else`() {
        val string = "if (a) {} else {}"
        val expected = """
            if (a) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }
}
