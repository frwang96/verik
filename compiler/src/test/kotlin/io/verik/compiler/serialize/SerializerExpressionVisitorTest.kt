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

package io.verik.compiler.serialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.assertOutputTextEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class SerializerExpressionVisitorTest : BaseTest() {

    @Test
    fun `parenthesized expression`() {
        val projectContext = TestDriver.serialize(
            """
            var x = 0
            var y = (x + 1) * x
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            
            int y = (x + 1) * x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `binary expression`() {
        val projectContext = TestDriver.serialize(
            """
            var x = 0
            var y = x + 1
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            
            int y = x + 1;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `reference expression`() {
        val projectContext = TestDriver.serialize(
            """
            var x = 0
            var y = x
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            
            int y = x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    @Disabled
    fun `call expression`() {
        TODO()
    }

    @Test
    fun `constant expression`() {
        val projectContext = TestDriver.serialize(
            """
            var x = 0
            """.trimIndent()
        )
        val expected = """
            int x = 0;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }
}