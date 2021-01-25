/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkCls
import verikc.vk.ast.VkConstructorFunction

internal class VkBuilderClsTest {

    @Test
    fun `class simple`() {
        val string = """
            class C: Class()
        """.trimIndent()
        val expected = VkCls(
            line(3),
            "C",
            Symbol(3),
            VkConstructorFunction(line(3), "i_C", Symbol(7)),
            listOf(),
            listOf()
        )
        assertEquals(
            expected,
            VkBuildUtil.buildCls("", string)
        )
    }
}