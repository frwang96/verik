/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ConstructorInterpreterStageTest : BaseTest() {

    @Test
    fun `constructor with parameter`() {
        driveElementTest(
            """
                class C(x: Int) : Class()
            """.trimIndent(),
            ConstructorInterpreterStage::class,
            """
                SvClass(
                    C, C, Class, [],
                    [SvConstructor(C, BlockExpression(*), [SvValueParameter(x, Int, null, INPUT)])],
                    0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `constructor with super type call expression`() {
        driveElementTest(
            """
                open class C : Class()
                class D : C()
            """.trimIndent(),
            ConstructorInterpreterStage::class,
            """
                SvClass(
                    D, D, C, [],
                    [SvConstructor(
                        D, BlockExpression(Unit, [CallExpression(Unit, new, SuperExpression(C), 0, [], [])]), []
                    )],
                    0
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }
}
