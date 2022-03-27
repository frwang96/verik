/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class StructLiteralTransformerStageTest : BaseTest() {

    @Test
    fun `struct literal`() {
        driveElementTest(
            """
                class S(val x: Boolean) : Struct()
                var s = S(false)
            """.trimIndent(),
            StructLiteralTransformerStage::class,
            "StructLiteralExpression(S, [StructLiteralEntry(x, ConstantExpression(*))])"
        ) { it.findExpression("s") }
    }
}
