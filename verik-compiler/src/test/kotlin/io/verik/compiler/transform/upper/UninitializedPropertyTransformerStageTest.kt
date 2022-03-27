/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class UninitializedPropertyTransformerStageTest : BaseTest() {

    @Test
    fun `uninitialized property`() {
        driveElementTest(
            """
                val x: Boolean = nc()
            """.trimIndent(),
            UninitializedPropertyTransformerStage::class,
            "Property(x, Boolean, null, 0, 0)"
        ) { it.findDeclaration("x") }
    }
}
