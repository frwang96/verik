/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.specialize.SpecializerStage
import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ConstantPropagatorSubstageTest : BaseTest() {

    @Test
    fun `constant expression`() {
        driveElementTest(
            """
                const val x = 0
                fun f() {
                    println(x)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, 0, [ConstantExpression(Int, 0)], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `constant reference expression`() {
        driveElementTest(
            """
                const val x = 0
                const val y = x
                fun f() {
                    println(y)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, 0, [ConstantExpression(Int, 0)], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `constant call expression`() {
        driveElementTest(
            """
                val y = b<TRUE>()
                fun f() {
                    println(y)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, 0, [ConstantExpression(Boolean, 1'b1)], [])"
        ) { it.findExpression("f") }
    }
}
