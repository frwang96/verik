/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PrimaryConstructorReducerStageTest : BaseTest() {

    @Test
    fun `reduce primary constructor simple`() {
        driveElementTest(
            """
                class C : Class()
            """.trimIndent(),
            PrimaryConstructorReducerStage::class,
            """
                KtClass(
                    C, C, Class, [],
                    [SecondaryConstructor(C, C, BlockExpression(Unit, []), [], CallExpression(*))],
                    null, 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `reduce primary constructor with property`() {
        val blockExpression = """
            BlockExpression(
                Unit,
                [KtBinaryExpression(
                    Unit,
                    ReferenceExpression(Int, x, ThisExpression(C), 0),
                    ReferenceExpression(Int, x, null, 0),
                    EQ
                )]
            )
        """.trimIndent()
        driveElementTest(
            """
                class C(val x: Int) : Class()
            """.trimIndent(),
            PrimaryConstructorReducerStage::class,
            """
                KtClass(
                    C, C, Class, [], [
                        Property(x, Int, null, 0, 0),
                        SecondaryConstructor(C, C, $blockExpression, [KtValueParameter(*)], CallExpression(*))
                    ], null, 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }
}
