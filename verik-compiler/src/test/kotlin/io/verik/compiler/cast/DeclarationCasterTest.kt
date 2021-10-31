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
        val projectContext = driveTest(
            CasterStage::class,
            """
                typealias N = INC<`7`>
            """.trimIndent()
        )
        assertElementEquals(
            "TypeAlias(N, INC<`7`>)",
            projectContext.findDeclaration("N")
        )
    }

    @Test
    fun `class simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, [], [], [], false, false, false, PrimaryConstructor(C, [], []), null)",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with class`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C {
                    class D
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [KtBasicClass(D, [], [], [], false, false, false, PrimaryConstructor(D, [], []), null)],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with function`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C {
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [KtFunction(f, Unit, *, [], [], [], false)],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with property`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C {
                    val x = false
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [KtProperty(x, Boolean, *, [])],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with companion object`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C { companion object }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [KtBasicClass(Companion, [], [], [], false, false, true, null, null)],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C, [], []),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with type parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C<T>
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [],
                    [TypeParameter(T, Any)],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C<T>, [], [T]),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with primary constructor`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C(val x: Int)
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(C, [KtValueParameter(x, Int, [], true)], []),
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with super type call entry`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                open class C(x: Int)
                class D : C(0)
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    D,
                    [],
                    [],
                    [],
                    false,
                    false,
                    false,
                    PrimaryConstructor(D, [], []),
                    SuperTypeCallEntry(<init>, [ConstantExpression(*)])
                )
            """.trimIndent(),
            projectContext.findDeclaration("D")
        )
    }

    @Test
    fun `enum class`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                enum class E { A }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(E, [KtEnumEntry(A, E, [])], [], [], true, false, false, PrimaryConstructor(E, [], []), null)",
            projectContext.findDeclaration("E")
        )
    }

    @Test
    fun `object simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                object O
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(O, [], [], [], false, false, true, null, null)",
            projectContext.findDeclaration("O")
        )
    }

    @Test
    fun `function simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [], [], [], false)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `function with value parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f(x: Int) {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [KtValueParameter(x, Int, [], false)], [], [], false)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `function with type parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun <N : `*`> f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [], [TypeParameter(N, `*`)], [], false)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `property simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = false
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Boolean, *, [])",
            projectContext.findDeclaration("x")
        )
    }
}
