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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FunctionInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret always seq block`() {
        val projectContext = driveTest(
            FunctionInterpreterStage::class,
            """
                class M: Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "AlwaysSeqBlock(f, EventControlExpression(*), KtBlockExpression(*))",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret always seq block error`() {
        assertThrows<TestErrorException> {
            driveTest(
                FunctionInterpreterStage::class,
                """
                class M: Module() {
                    @Seq
                    fun f() {}
                }
                """.trimIndent()
            )
        }.apply { assertEquals("On expression expected", message) }
    }

    @Test
    fun `interpret initial block`() {
        val projectContext = driveTest(
            FunctionInterpreterStage::class,
            """
                class M: Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "InitialBlock(f, KtBlockExpression(*))",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret task`() {
        val projectContext = driveTest(
            FunctionInterpreterStage::class,
            """
                @Task
                fun t() {}
            """.trimIndent()
        )
        assertElementEquals(
            "Task(t, *, [])",
            projectContext.findDeclaration("t")
        )
    }

    @Test
    fun `interpret function`() {
        val projectContext = driveTest(
            FunctionInterpreterStage::class,
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "SvFunction(f, Unit, *, false, REGULAR, [])",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `interpret function in class`() {
        val projectContext = driveTest(
            FunctionInterpreterStage::class,
            """
                class C {
                    fun f() {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SvFunction(f, Unit, *, false, VIRTUAL, [])",
            projectContext.findDeclaration("f")
        )
    }
}
