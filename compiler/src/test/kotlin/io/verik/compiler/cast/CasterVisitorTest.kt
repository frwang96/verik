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
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.TestException
import io.verik.compiler.util.assertElementEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CasterVisitorTest: BaseTest() {

    @Test
    fun `empty file`() {
        val projectContext = TestDriver.cast("")
        assertElementEquals(
            "File([])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `class simple`() {
        val projectContext = TestDriver.cast("""
            class C
        """.trimIndent())
        assertElementEquals(
            "File([BaseClass(C, [], [])])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `classes simple`() {
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
    fun `function simple`() {
        val projectContext = TestDriver.cast("""
            class C {
                fun f() {}
            }
        """.trimIndent())
        assertElementEquals(
            "File([BaseClass(C, [BaseFunction(f, null)], [])])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `function annotation`() {
        val projectContext = TestDriver.cast("""
            class C {
                @task fun f() {}
            }
        """.trimIndent())
        assertElementEquals(
            "File([BaseClass(C, [BaseFunction(f, task)], [])])",
            projectContext.vkFiles.first()
        )
    }

    @Test
    fun `function annotations conflicting`() {
        assertThrows<TestException> {
            TestDriver.cast("""
                class C {
                    @com @seq fun f() {}
                }
            """.trimIndent())
        }.apply {
            assertEquals("Conflicting annotations: com, seq", message)
        }
    }

    @Test
    fun `property simple`() {
        val projectContext = TestDriver.cast("""
            class C {
                val x = false
            }
        """.trimIndent())
        assertElementEquals(
            "File([BaseClass(C, [], [BaseProperty(x, Boolean)])])",
            projectContext.vkFiles.first()
        )
    }
}