/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CoverPropertyInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret cover point`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverPoint(cp, ReferenceExpression(Boolean, x, null, 0), [])"
        ) { it.findDeclaration("cp") }
    }

    @Test
    fun `interpret cover point with bin`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x, "bins b = {1'b0}")
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverPoint(cp, ReferenceExpression(Boolean, x, null, 0), [StringTemplateExpression(String, *)])"
        ) { it.findDeclaration("cp") }
    }

    @Test
    fun `interpret cover cross`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp, cp)
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverCross(cc, [cp, cp], [])"
        ) { it.findDeclaration("cc") }
    }
}
