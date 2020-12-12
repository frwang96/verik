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
import verikc.assertThrowsMessage
import verikc.base.ast.LineException

internal class LangModuleDataTest {

    @Test
    fun `function ubit int illegal`() {
        val string = "ubit(0)"
        assertThrowsMessage<LineException>("could not infer width of ubit") {
            LangModuleUtil.buildExpressionWithContext(string)
        }
    }

    @Test
    fun `function ubit int`() {
        val string = "x + ubit(0)"
        val expected = "x + 8'h00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function ubit int int`() {
        val string = "ubit(8, 0)"
        val expected = "8'h00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function sbit int illegal`() {
        val string = "sbit(0)"
        assertThrowsMessage<LineException>("could not infer width of sbit") {
            LangModuleUtil.buildExpressionWithContext(string)
        }
    }

    @Test
    fun `function sbit int int`() {
        val string = "sbit(8, 0)"
        val expected = "8'sh00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }
}