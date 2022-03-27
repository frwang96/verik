/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class InitializerBlockReducerStageTest : BaseTest() {

    @Test
    fun `reduce initializer block`() {
        driveElementTest(
            """
                class C : Class() {
                    init {
                        println()
                    }
                }
            """.trimIndent(),
            InitializerBlockReducerStage::class,
            "SvClass(C, C, Class, [], [SvConstructor(C, BlockExpression(Unit, [CallExpression(*)]), [])], 0)"
        ) { it.findDeclaration("C") }
    }
}
