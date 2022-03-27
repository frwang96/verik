/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CoverGroupInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret cover group`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup()
            """.trimIndent(),
            CoverGroupInterpreterStage::class,
            """
                CoverGroup(
                    CG, CG, [], [],
                    SvConstructor(CG, BlockExpression(Unit, []), [SvValueParameter(x, Boolean, null, REF)])
                )
            """.trimIndent()
        ) { it.findDeclaration("CG") }
    }
}
