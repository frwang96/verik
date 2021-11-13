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
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class DeclarationSpecializerStageTest : BaseTest() {

    @Test
    fun `specialize class type parameter cardinal`() {
        val projectContext = driveTest(
            DeclarationSpecializerStage::class,
            """
                class C<N : `*`>
                val c = C<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C_8, C_8, [], [], [], 0, 0, 0, PrimaryConstructor(C_8, [], []), null)",
            projectContext.findDeclaration("C_8")
        )
    }

    @Test
    fun `specialize class type parameter class`() {
        val projectContext = driveTest(
            DeclarationSpecializerStage::class,
            """
                class C
                class D<T>
                val d = D<C>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(D_C, D_C, [], [], [], 0, 0, 0, PrimaryConstructor(D_C, [], []), null)",
            projectContext.findDeclaration("D_C")
        )
    }

    @Test
    fun `specialize class with property`() {
        val projectContext = driveTest(
            DeclarationSpecializerStage::class,
            """
                class C<N : `*`> {
                    val x: Ubit<N> = nc()
                }
                val c = C<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C_8, C_8,
                    [KtProperty(x, Ubit<`8`>, KtCallExpression(*), [], 0)],
                    [], [], 0, 0, 0, *, null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C_8")
        )
    }

    @Test
    fun `specialize function type parameter`() {
        val projectContext = driveTest(
            DeclarationSpecializerStage::class,
            """
                fun <N : `*`> f() {}
                val x = f<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f_8, Unit, KtBlockExpression(*), [], [], [], 0)",
            projectContext.findDeclaration("f_8")
        )
    }

    @Test
    fun `specialize property type parameter`() {
        val projectContext = driveTest(
            DeclarationSpecializerStage::class,
            """
                class C
                class D<E> {
                    val e : E = nc()
                }
                val d = D<C>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(e, C, KtCallExpression(*), [], 0)",
            projectContext.findDeclaration("e")
        )
    }
}
