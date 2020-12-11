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

package verikc.al

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.base.ast.Symbol

internal class AlNodeTest {

    @Test
    fun `rules count`() {
        val rule = AlRuleParser.parseKotlinFile(Symbol.NULL, "val x = 0")
        assertEquals(25, rule.countRuleNodes())
    }

    @Test
    fun `tokens count`() {
        val rule = AlRuleParser.parseKotlinFile(Symbol.NULL, "val x = 0")
        assertEquals(3, rule.countTokenNodes())
    }

    @Test
    fun `to string`() {
        val rule = AlRuleParser.parseKotlinFile(Symbol.NULL, "")
        val expected = """
            KOTLIN_FILE
            ├─ PACKAGE_HEADER
            └─ IMPORT_LIST
        """.trimIndent()
        assertStringEquals(expected, rule)
    }
}
