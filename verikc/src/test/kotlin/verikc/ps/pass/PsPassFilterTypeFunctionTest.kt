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

package verikc.ps.pass

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.ps.PsPassUtil

internal class PsPassFilterTypeFunctionTest {

    @Test
    fun `filter type function`() {
        val string = """
            fun f(x: _ubit) {
                type(x, _ubit(8))
            }
        """.trimIndent()
        val methodBlock = PsPassUtil.passModuleMethodBlock("", "", string)
        assertEquals(0, methodBlock.block.expressions.size)
    }
}