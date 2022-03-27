/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ClassInterpreterStageTest : BaseTest() {

    @Test
    fun `class simple`() {
        driveElementTest(
            """
                class C : Class()
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    C, C, Class, [],
                    [SecondaryConstructor(C, C, BlockExpression(*), [], CallExpression(Class, Class, null, 0, [], []))],
                    0
                )
            """.trimIndent(),
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class declarations static`() {
        driveElementTest(
            """
                object O : Class()
            """.trimIndent(),
            ClassInterpreterStage::class,
            "SvClass(O, O, Class, [], [], 1)"
        ) { it.findDeclaration("O") }
    }
}
