/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ConstantPropagatorStageTest : BaseTest() {

    @Test
    fun `constant expression type parameterized`() {
        driveElementTest(
            """
                class C<N : `*`> : Class() {
                    val x = i<N>()
                }
                val y = C<`8`>().x
            """.trimIndent(),
            ConstantPropagatorStage::class,
            "ConstantExpression(Int, 8)"
        ) { it.findExpression("y") }
    }
}
