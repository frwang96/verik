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

internal class DeclarationCasterTest : BaseTest() {

    @Test
    fun `type alias simple`() {
        driveElementTest(
            """
                typealias X = INC<`7`>
            """.trimIndent(),
            CasterStage::class,
            "TypeAlias(X, INC<`7`>, [])"
        ) { it.findDeclaration("X") }
    }

    @Test
    fun `type alias with type parameter`() {
        driveElementTest(
            """
                typealias X<Y> = INC<Y>
            """.trimIndent(),
            CasterStage::class,
            "TypeAlias(X, INC<Y>, [TypeParameter(Y, Any)])"
        ) { it.findDeclaration("X") }
    }

    @Test
    fun `class simple`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "KtClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, C, [], []), null)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with class`() {
        driveElementTest(
            """
                class C {
                    class D
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C,
                    [KtClass(D, D, [], [], [], 0, 0, 0, PrimaryConstructor(D, D, [], []), null)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with function`() {
        driveElementTest(
            """
                class C {
                    fun f() {}
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C,
                    [KtFunction(f, Unit, *, [], [], [], 0)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with property`() {
        driveElementTest(
            """
                class C {
                    val x = false
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C,
                    [KtProperty(x, Boolean, *, [], 0)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with companion object`() {
        driveElementTest(
            """
                class C { companion object }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C,
                    [KtClass(Companion, Companion, [], [], [], 0, 0, 1, null, null)],
                    [], [], 0, 0, 0,
                    PrimaryConstructor(C, C, [], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with type parameter`() {
        driveElementTest(
            """
                class C<T>
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C<T>, [],
                    [TypeParameter(T, Any)],
                    [], 0, 0, 0,
                    PrimaryConstructor(C, C<T>, [], [T]),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor`() {
        driveElementTest(
            """
                class C(val x: Int)
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C, [], [], [], 0, 0, 0,
                    PrimaryConstructor(C, C, [KtValueParameter(x, Int, [], 1, 0)], []),
                    null
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with super type call entry`() {
        driveElementTest(
            """
                open class C(x: Int)
                class D : C(0)
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    D, D, [], [], [], 0, 0, 0,
                    PrimaryConstructor(D, D, [], []),
                    CallExpression(C, C, null, [ConstantExpression(*)], [])
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }

    @Test
    fun `enum class`() {
        driveElementTest(
            """
                enum class E { A }
            """.trimIndent(),
            CasterStage::class,
            "KtClass(E, E, [KtEnumEntry(A, E, [])], [], [], 1, 0, 0, PrimaryConstructor(E, E, [], []), null)"
        ) { it.findDeclaration("E") }
    }

    @Test
    fun `object simple`() {
        driveElementTest(
            """
                object O
            """.trimIndent(),
            CasterStage::class,
            "KtClass(O, O, [], [], [], 0, 0, 1, null, null)"
        ) { it.findDeclaration("O") }
    }

    @Test
    fun `function simple`() {
        driveElementTest(
            """
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with value parameter`() {
        driveElementTest(
            """
                fun f(x: Int) {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [KtValueParameter(x, Int, [], 0, 0)], [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with type parameter`() {
        driveElementTest(
            """
                fun <X : `*`> f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [TypeParameter(X, `*`)], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `property simple`() {
        driveElementTest(
            """
                var x = false
            """.trimIndent(),
            CasterStage::class,
            "KtProperty(x, Boolean, *, [], 1)"
        ) { it.findDeclaration("x") }
    }
}
