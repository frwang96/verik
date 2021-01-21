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

package verikc.tx.build

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.tx.TxBuildUtil

internal class TxBuilderExpressionSimpleTest {

    @Test
    fun `arithmetic precedence ordered`() {
        val moduleContext = """
            val x = t_Int()
            val y = t_Int()
            val z = t_Int()
        """.trimIndent()
        val string = """
            x + y * z
        """.trimIndent()
        val expected = """
            x + y * z;
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val moduleContext = """
            val x = t_Int()
            val y = t_Int()
            val z = t_Int()
        """.trimIndent()
        val string = """
            x * (y + z)
        """.trimIndent()
        val expected = """
            x * (y + z);
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val moduleContext = """
            val x = t_Int()
            val y = t_Int()
            val z = t_Int()
        """.trimIndent()
        val string = """
            x - y + z
        """.trimIndent()
        val expected = """
            x - y + z;
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val moduleContext = """
            val x = t_Int()
            val y = t_Int()
            val z = t_Int()
        """.trimIndent()
        val string = """
            x - (y + z)
        """.trimIndent()
        val expected = """
            x - (y + z);
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `if expression`() {
        val moduleContext = """
            val x = t_Int()
        """.trimIndent()
        val string = """
            x = if (true) 1 else 0
        """.trimIndent()
        val expected = """
            x = 1'b1 ? 1 : 0;
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }
}
