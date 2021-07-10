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

package io.verik.compiler.interpret

import io.verik.compiler.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FunctionInterpreterTest : BaseTest() {

    @Test
    fun `interpret function`() {
        val projectContext = TestDriver.interpret(
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "SFunction(f, Unit, *)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret function in class`() {
        val projectContext = TestDriver.interpret(
            """
                class C {
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SFunction(f, Unit, *)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret initial block`() {
        val projectContext = TestDriver.interpret(
            """
                class M: Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SInitialBlock(f, KBlockExpression(*))",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret always seq block`() {
        val projectContext = TestDriver.interpret(
            """
                class M: Module() {
                    var x = false
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SAlwaysSeqBlock(f, SEventControlExpression(*), KBlockExpression(*))",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret always seq block illegal`() {
        assertThrows<TestException> {
            TestDriver.interpret(
                """
                class M: Module() {
                    @Seq
                    fun f() {}
                }
            """.trimIndent()
            )
        }.apply { assertEquals("On expression expected", message) }
    }
}