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

package verikc.vk.check

import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.vk.VkBuildUtil

internal class VkCheckerClsTest {

    @Test
    fun `setval not in init`() {
        val string = """
            class C: Class() {
                val x = t_Boolean()
                fun init() {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("init must contain setval function to initialize immutable properties") {
            VkBuildUtil.buildCls("", string)
        }
    }

    @Test
    fun `setval in function`() {
        val string = """
            class C: Class() {
                val x = t_Boolean()
                fun init() { setval(false) }
                fun f() { setval(false) }
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("method block cannot contain setval function") {
            VkBuildUtil.buildCls("", string)
        }
    }
}