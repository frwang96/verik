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

package verik.core.lang.modules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.sv.SvUtil

internal class LangModuleStringTest {

    @Test
    fun `function print string`() {
        val rule = AlRuleParser.parseExpression("print(\"0\")")
        val expected = "\$write(\"0\")"
        assertEquals(expected, SvUtil.buildExpression(rule))
    }

    @Test
    fun `function print int`() {
        val rule = AlRuleParser.parseExpression("print(0)")
        val expected = "\$write(\"%0d\", 0)"
        assertEquals(expected, SvUtil.buildExpression(rule))
    }

    @Test
    fun `function println string`() {
        val rule = AlRuleParser.parseExpression("println(\"0\")")
        val expected = "\$display(\"0\")"
        assertEquals(expected, SvUtil.buildExpression(rule))
    }

    @Test
    fun `function println bool`() {
        val rule = AlRuleParser.parseExpression("print(false)")
        val expected = "\$write(\"%b\", 1'b0)"
        assertEquals(expected, SvUtil.buildExpression(rule))
    }
}