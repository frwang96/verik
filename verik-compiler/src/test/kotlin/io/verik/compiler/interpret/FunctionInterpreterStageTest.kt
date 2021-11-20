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
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class FunctionInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret always seq block`() {
        driveTest(
            """
                class M: Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "AlwaysSeqBlock(f, EventControlExpression(*), KtBlockExpression(*))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret always seq block error`() {
        driveTest(
            """
            class M: Module() {
                @Seq
                fun f() {}
            }
            """.trimIndent(),
            true,
            "On expression expected"
        )
    }

    @Test
    fun `interpret initial block`() {
        driveTest(
            """
                class M: Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "InitialBlock(f, KtBlockExpression(*))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret task`() {
        driveTest(
            """
                @Task
                fun t() {}
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "Task(t, *, [])"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `interpret function`() {
        driveTest(
            """
                fun f() {}
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], REGULAR, 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret function in class`() {
        driveTest(
            """
                class C {
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], VIRTUAL, 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret function in object`() {
        driveTest(
            """
                object O {
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], REGULAR, 1)"
        ) { it.findDeclaration("f") }
    }
}
