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

package io.verik.compiler.cast

import io.verik.compiler.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CasterUtilTest: BaseTest() {

    @Test
    fun `type class simple`() {
        val projectContext = TestDriver.cast("""
            var x = 0
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Int)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type class parameterized`() {
        val projectContext = TestDriver.cast("""
            var x = listOf(0)
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, List<Int>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type nullable`() {
        assertThrows<TestException> {
            TestDriver.cast("""
                @Suppress("ImplicitNullableNothingType")
                var x = null
            """.trimIndent())
        }.apply {
            assertEquals("Nullable type not supported: Nothing?", message)
        }
    }

    @Test
    fun `type reference simple`() {
        val projectContext = TestDriver.cast("""
            var x: Int = 0
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Int)",
            projectContext.findDeclaration("x")
        )
    }
}