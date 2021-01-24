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

package verikc.tx.build

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.tx.TxBuildUtil

internal class TxBuilderStructTest {

    @Test
    fun `struct simple`() {
        val string = """
            class S: Struct() {
                val x = t_Boolean()
            }
        """.trimIndent()
        val expected = """
            typedef struct {
                logic x;
            } S;
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildStruct("", string))
    }
}