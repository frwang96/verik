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

internal class TxBuilderExpressionBaseTest {

    @Test
    fun `forever block`() {
        val string = """
            forever { 0 }
        """.trimIndent()
        val expected = """
            forever begin
                0;
            end
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `if block`() {
        val moduleContext = """
            val x = t_Boolean()
        """.trimIndent()
        val string = """
            if (x) {}
        """.trimIndent()
        val expected = """
            if (x) begin
            end
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `if else block`() {
        val moduleContext = """
            val x = t_Boolean()
        """.trimIndent()
        val string = """
            if (x) {} else {}
        """.trimIndent()
        val expected = """
            if (x) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }

    @Test
    fun `if else chained block`() {
        val moduleContext = """
            val x = t_Boolean()
            val y = t_Boolean()
        """.trimIndent()
        val string = """
            if (x) {} else if (y) {} else {}
        """.trimIndent()
        val expected = """
            if (x) begin
            end
            else if (y) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleActionBlockExpression("", moduleContext, string)
        )
    }
}
