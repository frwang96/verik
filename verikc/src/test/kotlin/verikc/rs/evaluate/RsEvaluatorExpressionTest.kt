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

package verikc.rs.evaluate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.rs.RsResolveUtil

internal class RsEvaluatorExpressionTest {

    @Test
    fun `evaluate function native add int int`() {
        val string = """
            val x = 2 + 1
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(3),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function native sub int int`() {
        val string = """
            val x = 2 - 1
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(1),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function native mul int int`() {
        val string = """
            val x = 2 * 1
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(2),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function native div int int`() {
        val string = """
            val x = 2 / 1
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(2),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function native rem int int`() {
        val string = """
            val x = 2 % 1
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(0),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function log int`() {
        val string = """
            val x = log(2)
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(1),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function log int invalid`() {
        val string = """
            val x = log(0)
        """.trimIndent()
        assertThrowsMessage<LineException>("illegal argument 0 to log function") {
            RsResolveUtil.resolveProperty("", string)
        }
    }

    @Test
    fun `evaluate function exp int`() {
        val string = """
            val x = exp(2)
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(4),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }

    @Test
    fun `evaluate function exp int invalid`() {
        val string = """
            val x = exp(32)
        """.trimIndent()
        assertThrowsMessage<LineException>("illegal argument 32 to exp function") {
            RsResolveUtil.resolveProperty("", string)
        }
    }

    @Test
    fun `evaluate property val`() {
        val fileContext = """
            val x = 0
        """.trimIndent()
        val string = """
            val y = x
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(0),
            RsResolveUtil.resolveProperty(fileContext, string).evaluateResult
        )
    }

    @Test
    fun `evaluate property var`() {
        val fileContext = """
            var x = 0
        """.trimIndent()
        val string = """
            val y = x
        """.trimIndent()
        assertEquals(
            null,
            RsResolveUtil.resolveProperty(fileContext, string).evaluateResult
        )
    }

    @Test
    fun `evaluate literal`() {
        val string = """
            val x = 0
        """.trimIndent()
        assertEquals(
            RsEvaluateResult(0),
            RsResolveUtil.resolveProperty("", string).evaluateResult
        )
    }
}