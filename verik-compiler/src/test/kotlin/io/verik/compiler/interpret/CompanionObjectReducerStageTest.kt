/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CompanionObjectReducerStageTest : BaseTest() {

    @Test
    fun `reduce companion object`() {
        driveElementTest(
            """
                class C : Class() {
                    companion object { var x = false }
                }
            """.trimIndent(),
            CompanionObjectReducerStage::class,
            """
                SvClass(
                    C, C, Class, [],
                    [SvConstructor(*), Property(x, Boolean, ConstantExpression(*), 1, 1)], 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }
}
