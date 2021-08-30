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
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class DeclarationCasterTest : BaseTest() {

    @Test
    fun `class with class`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                class C {
                    class D
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, false, [], [KtBasicClass(D, false, [], [])])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with function`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                class C {
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, false, [], [KtFunction(f, Unit, *, [])])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with property`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                class C {
                    val x = false
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, false, [], [KtProperty(x, Boolean, *)])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with companion object`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                class C { companion object }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, false, [], [KtBasicClass(Companion, false, [], [])])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with type parameter`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                class C<T>
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, false, [TypeParameter(T, Any)], [])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `enum class`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                enum class E { A }
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(E, true, [], [KtEnumEntry(A, E)])",
            projectContext.findDeclaration("E")
        )
    }

    @Test
    fun `function simple`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [])",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `function annotation`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                @Task
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [Task])",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `property simple`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                var x = false
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Boolean, *)",
            projectContext.findDeclaration("x")
        )
    }
}
