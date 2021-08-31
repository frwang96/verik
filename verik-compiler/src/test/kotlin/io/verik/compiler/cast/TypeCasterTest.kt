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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeCasterTest : BaseTest() {

    @Test
    fun `type class simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Int, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    @Disabled
    // TODO resolve constructor
    fun `type class parameterized`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C<T>
                var x = C<Int>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, C<Int>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    @Disabled
    // TODO resolve constructor
    fun `type type parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C<T> {
                    val x = C<T>()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, C<T>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    @Disabled
    // TODO add function parameters
    fun `type nullable`() {
        assertThrows<TestErrorException> {
            driveTest(
                CasterStage::class,
                """
                    fun f(x: Int?) {}
                """.trimIndent()
            )
        }.apply {
            assertEquals("Nullable type not supported: Int?", message)
        }
    }

    @Test
    fun `type reference simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Int = 0
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Int, ConstantExpression(Int, 0))",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    @Disabled
    // TODO resolve constructor
    fun `type reference type parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C<T> {
                    val x: C<T> = C()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, C<T>)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Ubit<`8`> = u(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`8`>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal auto`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Ubit<`*`> = u(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`*`>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal cardinal`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Ubit<Cardinal> = u(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`*`>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal function`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Ubit<ADD<`8`, `16`>> = u(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<ADD<`8`, `16`>>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal function invalid`() {
        assertThrows<TestErrorException> {
            driveTest(
                CasterStage::class,
                """
                    var x: Ubit<ADD<`8`, Int>> = u(0)
                """.trimIndent()
            )
        }.apply {
            assertEquals("Cardinal type expected", message)
        }
    }

    @Test
    fun `type reference cardinal type parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C<N: Cardinal> {
                    var x: Ubit<N> = u(0).ext()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<N>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `type reference cardinal type parameter invalid`() {
        assertThrows<TestErrorException> {
            driveTest(
                CasterStage::class,
                """
                    class C<N> {
                        var x: Ubit<INC<N>> = u(0)
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals("Cardinal type expected", message)
        }
    }
}
