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

internal class TypeCasterTest: BaseTest() {

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
    fun `type type parameter`() {
        val projectContext = TestDriver.cast("""
            class C<T> {
                val x = listOf<T>()
            }
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, List<T>)",
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

    @Test
    fun `type reference type parameter`() {
        val projectContext = TestDriver.cast("""
            class C<T> {
                val x: List<T> = listOf()
            }
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, List<T>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal simple`() {
        val projectContext = TestDriver.cast("""
            var x: Ubit<`8`> = u(0)
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Ubit<`8`>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal invalid`() {
        assertThrows<TestException> {
            TestDriver.cast("""
            var x: Ubit<Cardinal> = u(0)
        """.trimIndent())
        }.apply {
            assertEquals("Cardinal type expected", message)
        }
    }

    @Test
    fun `type reference cardinal function`() {
        val projectContext = TestDriver.cast("""
            var x: Ubit<ADD<`8`, `16`>> = u(0)
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Ubit<ADD<`8`, `16`>>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal function invalid`() {
        assertThrows<TestException> {
            TestDriver.cast("""
                var x: Ubit<ADD<`8`, Int>> = u(0)
            """.trimIndent())
        }.apply {
            assertEquals("Cardinal type expected", message)
        }
    }

    @Test
    fun `type reference cardinal type parameter`() {
        val projectContext = TestDriver.cast("""
            class C<N: Cardinal> {
                var x: Ubit<N> = u(0)
            }
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Ubit<N>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal type parameter invalid`() {
        assertThrows<TestException> {
            TestDriver.cast("""
                class C<N> {
                    var x: Ubit<INC<N>> = u(0)
                }
            """.trimIndent())
        }.apply {
            assertEquals("Cardinal type parameter expected", message)
        }
    }
}