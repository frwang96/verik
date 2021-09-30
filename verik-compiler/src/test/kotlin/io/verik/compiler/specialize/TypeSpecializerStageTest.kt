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

package io.verik.compiler.specialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeSpecializerStageTest : BaseTest() {

    @Test
    fun `property typealias`() {
        val projectContext = driveTest(
            TypeSpecializerStage::class,
            """
                typealias N = `8`
                var x: Ubit<N> = u(0x00)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`8`>, *, [])",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `property typealias nested`() {
        val projectContext = driveTest(
            TypeSpecializerStage::class,
            """
                typealias N = `8`
                typealias M = N
                var x: Ubit<M> = u(0x00)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`8`>, *, [])",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `property typealias function nested`() {
        val projectContext = driveTest(
            TypeSpecializerStage::class,
            """
                typealias N = `8`
                typealias M = ADD<N, N>
                var x: Ubit<M> = u(0x00)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`16`>, *, [])",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `property cardinal out of range`() {
        assertThrows<TestErrorException> {
            driveTest(
                TypeSpecializerStage::class,
                """
                    var x: Ubit<EXP<`32`>> = u0()
                """.trimIndent()
            )
        }.apply { assertEquals("Cardinal type out of range", message) }
    }
}
