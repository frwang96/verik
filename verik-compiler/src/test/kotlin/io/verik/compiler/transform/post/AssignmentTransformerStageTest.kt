/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class AssignmentTransformerStageTest : BaseTest() {

    @Test
    fun `transform assign`() {
        driveElementTest(
            """
                var x = false
                fun f() {
                    x = true
                }
            """.trimIndent(),
            AssignmentTransformerStage::class,
            "SvBinaryExpression(Void, ReferenceExpression(*), ConstantExpression(*), ASSIGN)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `transform arrow assign seq block`() {
        driveElementTest(
            """
                class M : Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        on(posedge(false)) {
                            x = true
                        }
                    }
                }
            """.trimIndent(),
            AssignmentTransformerStage::class,
            "SvBinaryExpression(Void, ReferenceExpression(*), ConstantExpression(*), ARROW_ASSIGN)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `transform arrow assign seq block port`() {
        driveElementTest(
            """
                class M(@Out var x: Ubit<`4`>) : Module() {
                    @Seq
                    fun f() {
                        on(posedge(false)) {
                            x[0] = true
                        }
                    }
                }
            """.trimIndent(),
            AssignmentTransformerStage::class,
            "SvBinaryExpression(Void, SvArrayAccessExpression(*), ConstantExpression(*), ARROW_ASSIGN)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `transform arrow assign clocking block`() {
        driveElementTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class M : Module() {
                    private var x = false
                    @Make
                    private var cb = CB(posedge(false), x)
                    @Run
                    fun f() {
                        cb.x = true
                    }
                }
            """.trimIndent(),
            AssignmentTransformerStage::class,
            "SvBinaryExpression(Void, ReferenceExpression(*), ConstantExpression(*), ARROW_ASSIGN)"
        ) { it.findExpression("f") }
    }
}
