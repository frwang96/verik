/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SpecialTypeResolverStageTest : BaseTest() {

    @Test
    fun `slice constant`() {
        driveElementTest(
            """
                var x = u(0x00)
                var y = x[1, 0]
            """.trimIndent(),
            SpecialTypeResolverStage::class,
            "CallExpression(Ubit<`*`>, get, ReferenceExpression(*), 0, [*], [`2`])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `slice illegal`() {
        driveMessageTest(
            """
                val x = u(0x00)
                var y = 0
                val z = x[y, 0]
            """.trimIndent(),
            true,
            "Unable to determine width of slice"
        )
    }

    @Test
    fun `cluster simple`() {
        driveElementTest(
            """
                class MI : ModuleInterface()
                class M : Module() {
                    @Make
                    val mi = cluster(4) { MI() }
                }
            """.trimIndent(),
            SpecialTypeResolverStage::class,
            "CallExpression(Cluster<`4`, MI>, cluster, null, 0, [*], [MI])"
        ) { it.findExpression("mi") }
    }
}
