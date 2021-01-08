/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.vk.ast.VkComponentInstance
import verikc.vk.ast.VkProperty

internal class VkBuilderComponentInstanceTest {

    @Test
    fun `component instance`() {
        val fileContext = """
            class _n: _module()
        """.trimIndent()
        val string = """
            @make val n = _n()
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(4), "n", Symbol(9), Symbol(3).toTypeGenerified()),
            listOf()
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, string)
        )
    }
}
