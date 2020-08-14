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

package verik.core.it

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.sv.SvFile
import verik.core.sv.SvModule

internal class ItFileTest {

    @Test
    fun `extract simple`() {
        val rule = AlRuleParser.parseKotlinFile("""
            package x
            class _m: _module
        """.trimIndent())
        val file = extractFile(rule)
        val expected = SvFile(listOf(SvModule(
                2,
                "m",
                listOf(),
                listOf()
        )))
        Assertions.assertEquals(expected, file)
    }
}