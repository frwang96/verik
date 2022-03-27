/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PropertyInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret constraint`() {
        driveElementTest(
            """
                @Cons
                val c = c(true)
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "Constraint(c, BlockExpression(Unit, [ConstantExpression(*)]))"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `interpret property static`() {
        driveElementTest(
            """
                object O : Class() {
                    var x = false
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "Property(x, Boolean, *, 1, 1)"
        ) { it.findDeclaration("x") }
    }
}
