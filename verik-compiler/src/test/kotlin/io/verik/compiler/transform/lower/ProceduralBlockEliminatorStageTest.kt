/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ProceduralBlockEliminatorStageTest : BaseTest() {

    @Test
    fun `eliminate procedural block`() {
        driveElementTest(
            """
                class M : Module() {
                    @Com
                    fun f() {}
                }
            """.trimIndent(),
            ProceduralBlockEliminatorStage::class,
            "Module(M, M, [], [])"
        ) { it.findDeclaration("M") }
    }
}
