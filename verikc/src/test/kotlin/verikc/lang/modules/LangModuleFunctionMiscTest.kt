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

internal class LangModuleFunctionMiscTest {

    @Test
    fun `function cat ubit`() {
        val string = """
            cat(ubit(8, 0x00))
        """.trimIndent()
        val expected = """
            {8'h00};
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", "", string))
    }

    @Test
    fun `function cat ubit ubit`() {
        val string = """
            cat(ubit(8, 0x00), ubit(8, 0xff))
        """.trimIndent()
        val expected = """
            {8'h00, 8'hff};
        """.trimIndent()
        assertStringEquals(expected, SvUtil.extractExpression("", "", string))
    }
}