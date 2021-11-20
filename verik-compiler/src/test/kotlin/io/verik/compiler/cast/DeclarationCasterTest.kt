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
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class DeclarationCasterTest : BaseTest() {

    @Test
    fun `type alias`() {
        driveTest(
            """
                typealias N = INC<`7`>
            """.trimIndent(),
            CasterStage::class,
            "TypeAlias(N, INC<`7`>)"
        ) { it.findDeclaration("N") }
    }

    @Test
    fun `class simple`() {
        driveTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "KtBasicClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, [], []), null)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with class`() {
        driveTest(
            """
                class C {
                    class D
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C,
                    [KtBasicClass(D, D, [], [], [], 0, 0, 0, PrimaryConstructor(D, [], []), null)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with function`() {
        driveTest(
            """
                class C {
                    fun f() {}
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C,
                    [KtFunction(f, Unit, *, [], [], [], 0)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with property`() {
        driveTest(
            """
                class C {
                    val x = false
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C,
                    [KtProperty(x, Boolean, *, [], 0)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with companion object`() {
        driveTest(
            """
                class C { companion object }
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C,
                    [KtBasicClass(Companion, Companion, [], [], [], 0, 0, 1, null, null)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with type parameter`() {
        driveTest(
            """
                class C<T>
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C<T>, [],
                    [TypeParameter(T, Any)],
                    [], 0, 0, 0,
                    PrimaryConstructor(C<T>, [], [T]),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor`() {
        driveTest(
            """
                class C(val x: Int)
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    C, C, [], [], [], 0, 0, 0,
                    PrimaryConstructor(C, [KtValueParameter(x, Int, [], 1, 0)], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with super type call entry`() {
        driveTest(
            """
                open class C(x: Int)
                class D : C(0)
            """.trimIndent(),
            CasterStage::class,
            """
                KtBasicClass(
                    D, D, [], [], [], 0, 0, 0,
                    PrimaryConstructor(D, [], []),
                    SuperTypeCallEntry(<init>, [ConstantExpression(*)])
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }

    @Test
    fun `enum class`() {
        driveTest(
            """
                enum class E { A }
            """.trimIndent(),
            CasterStage::class,
            "KtBasicClass(E, E, [KtEnumEntry(A, E, [])], [], [], 1, 0, 0, PrimaryConstructor(E, [], []), null)"
        ) { it.findDeclaration("E") }
    }

    @Test
    fun `object simple`() {
        driveTest(
            """
                object O
            """.trimIndent(),
            CasterStage::class,
            "KtBasicClass(O, O, [], [], [], 0, 0, 1, null, null)"
        ) { it.findDeclaration("O") }
    }

    @Test
    fun `function simple`() {
        driveTest(
            """
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with value parameter`() {
        driveTest(
            """
                fun f(x: Int) {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [KtValueParameter(x, Int, [], 0, 0)], [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with type parameter`() {
        driveTest(
            """
                fun <N : `*`> f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [TypeParameter(N, `*`)], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `property simple`() {
        driveTest(
            """
                var x = false
            """.trimIndent(),
            CasterStage::class,
            "KtProperty(x, Boolean, *, [], 1)"
        ) { it.findDeclaration("x") }
    }
}
