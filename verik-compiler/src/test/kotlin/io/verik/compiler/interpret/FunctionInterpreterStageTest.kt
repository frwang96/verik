/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class FunctionInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret always seq block`() {
        driveElementTest(
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
            "AlwaysSeqBlock(f, EventControlExpression(*), BlockExpression(*))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret always seq block if expression true`() {
        driveElementTest(
            """
                class M: Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        @Suppress("ConstantConditionIf")
                        if (true) on (posedge(x)) {}
                    }
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "AlwaysSeqBlock(f, EventControlExpression(*), BlockExpression(Unit, []))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret always seq block if expression false`() {
        driveElementTest(
            """
                class M: Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        @Suppress("ConstantConditionIf")
                        if (false) on (posedge(x)) {}
                    }
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "AlwaysSeqBlock(f, EventControlExpression(*), BlockExpression(Unit, []))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret always seq block error expected on`() {
        driveMessageTest(
            """
            class M: Module() {
                @Seq
                fun f() {
                    println()
                }
            }
            """.trimIndent(),
            true,
            "Expected on expression"
        )
    }

    @Test
    fun `interpret always seq block error insufficient arguments`() {
        driveMessageTest(
            """
            class M: Module() {
                @Seq
                fun f() {
                    on {}
                }
            }
            """.trimIndent(),
            true,
            "Insufficient arguments to call expression: on"
        )
    }

    @Test
    fun `interpret initial block`() {
        driveElementTest(
            """
                class M: Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "InitialBlock(f, BlockExpression(*))"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret task`() {
        driveElementTest(
            """
                @Task
                fun t() {}
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "Task(t, *, [], [], 0)"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `interpret task with return`() {
        driveElementTest(
            """
                @Task
                fun t(): Boolean { return false }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "Task(t, *, [], [SvValueParameter(<tmp>, Boolean, null, OUTPUT)], 0)"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `interpret function`() {
        driveElementTest(
            """
                fun f() {}
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], [], 0, 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret function in class`() {
        driveElementTest(
            """
                class C : Class() {
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], [], 0, 1)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret function in object`() {
        driveElementTest(
            """
                object O : Class() {
                    fun f() {}
                }
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "SvFunction(f, Unit, *, [], [], 1, 0)"
        ) { it.findDeclaration("f") }
    }
}
