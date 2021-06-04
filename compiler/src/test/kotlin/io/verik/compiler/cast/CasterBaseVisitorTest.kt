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

internal class CasterBaseVisitorTest: BaseTest() {

    @Test
    fun `file empty`() {
        val projectContext = TestDriver.cast("")
        assertElementEquals(
            "File([])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `file class`() {
        val projectContext = TestDriver.cast("""
            class C
        """.trimIndent())
        assertElementEquals(
            "File([BaseClass(C, [], [])])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `file classes`() {
        val projectContext = TestDriver.cast("""
            class C
            class D
        """.trimIndent())
        assertElementEquals(
            """
                File([
                    BaseClass(C, [], []),
                    BaseClass(D, [], [])
                ])
            """.trimIndent(),
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `class with class`() {
        val projectContext = TestDriver.cast("""
            class C {
                class D
            }
        """.trimIndent())
        assertElementEquals(
            "BaseClass(C, [], [BaseClass(D, [], [])])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with function`() {
        val projectContext = TestDriver.cast("""
            class C {
                fun f() {}
            }
        """.trimIndent())
        assertElementEquals(
            "BaseClass(C, [], [BaseFunction(f, Unit, null, *)])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with property`() {
        val projectContext = TestDriver.cast("""
            class C {
                val x = false
            }
        """.trimIndent())
        assertElementEquals(
            "BaseClass(C, [], [BaseProperty(x, Boolean, *)])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with companion object`() {
        val projectContext = TestDriver.cast("""
            class C { companion object }
        """.trimIndent())
        assertElementEquals(
            "BaseClass(C, [], [BaseClass(Companion, [], [])])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `class with type parameter`() {
        val projectContext = TestDriver.cast("""
            class C<T>
        """.trimIndent())
        assertElementEquals(
            "BaseClass(C, [TypeParameter(T, Any)], [])",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `function simple`() {
        val projectContext = TestDriver.cast("""
            fun f() {}
        """.trimIndent())
        assertElementEquals(
            "BaseFunction(f, Unit, null, *)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `function annotation`() {
        val projectContext = TestDriver.cast("""
            @task fun f() {}
        """.trimIndent())
        assertElementEquals(
            "BaseFunction(f, Unit, TASK, *)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `function annotations conflicting`() {
        assertThrows<TestException> {
            TestDriver.cast("""
                @com @seq fun f() {}
            """.trimIndent())
        }.apply {
            assertEquals("Conflicting annotations: com, seq", message)
        }
    }

    @Test
    fun `property simple`() {
        val projectContext = TestDriver.cast("""
            var x = false
        """.trimIndent())
        assertElementEquals(
            "BaseProperty(x, Boolean, *)",
            projectContext.findDeclaration("x")
        )
    }
}