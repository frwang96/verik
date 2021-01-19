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

internal class TxBuilderPortTest {

    @Test
    fun `primary property`() {
        val string = """
            val x = 0
        """.trimIndent()
        val expected = """
            int x = 0;
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildPrimaryProperty("", string)
        )
    }

    @Test
    fun `bus type`() {
        val fileContext = """
            class _b: _bus()
        """.trimIndent()
        val string = "@bus val b = _b()"
        val expected = """
            b b
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModulePort(fileContext, string)
        )
    }
}