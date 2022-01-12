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

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeCasterTest : BaseTest() {

    @Test
    fun `type class simple`() {
        driveElementTest(
            """
                var x = 0
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Int, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type class parameterized`() {
        driveElementTest(
            """
                class C<T>
                var x = C<Int>()
            """.trimIndent(),
            CasterStage::class,
            "Property(x, C<Int>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type type parameter`() {
        driveElementTest(
            """
                class C<T> {
                    val x = C<T>()
                }
            """.trimIndent(),
            CasterStage::class,
            "Property(x, C<T>, [], *, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference simple`() {
        driveElementTest(
            """
                var x: Int = 0
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Int, [], ConstantExpression(Int, 0), 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference type parameter`() {
        driveElementTest(
            """
                class C<T> {
                    val x: C<T> = C()
                }
            """.trimIndent(),
            CasterStage::class,
            "Property(x, C<T>, [], *, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal simple`() {
        driveElementTest(
            """
                var x: Ubit<`8`> = u(0)
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Ubit<`8`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal symbolic`() {
        driveElementTest(
            """
                var x: Ubit<`*`> = u(0)
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Ubit<`*`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal cardinal`() {
        driveElementTest(
            """
                var x: Ubit<Cardinal> = u(0)
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Ubit<`*`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal function`() {
        driveElementTest(
            """
                var x: Ubit<ADD<`8`, `16`>> = u(0)
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Ubit<ADD<`8`, `16`>>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal type parameter`() {
        driveElementTest(
            """
                class C<N: Cardinal> {
                    var x: Ubit<N> = u(0).ext()
                }
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Ubit<N>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `type reference cardinal type alias`() {
        driveElementTest(
            """
                typealias U = Ubit<`8`>
                var x: U = nc()
            """.trimIndent(),
            CasterStage::class,
            "Property(x, U, [], *, 1)"
        ) { it.findDeclaration("x") }
    }
}
