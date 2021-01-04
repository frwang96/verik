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

internal class TxBuilderBlockTest {

    @Test
    fun `block empty`() {
        val expected = """
            begin
            end
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildBlock("", "", ""))
    }

    @Test
    fun `block with statement`() {
        val string = """
            0
        """.trimIndent()
        val expected = """
            begin
                0;
            end
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildBlock("", "", string))
    }

    @Test
    fun `block with local property`() {
        val string = """
            val x = 0
        """.trimIndent()
        val expected = """
            begin
                automatic int x;
                x = 0;
            end
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildBlock("", "", string))
    }
}