/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CoreFunctionOverrideTransformerStageTest : BaseTest() {

    @Test
    fun `interpret function preRandomize`() {
        driveElementTest(
            """
                class C : Class() {
                    override fun preRandomize() {}
                }
            """.trimIndent(),
            CoreFunctionOverrideTransformerStage::class,
            "SvFunction(pre_randomize, Unit, BlockExpression(Unit, []), [], [], 0, 0)"
        ) { it.findDeclaration("pre_randomize") }
    }
}
