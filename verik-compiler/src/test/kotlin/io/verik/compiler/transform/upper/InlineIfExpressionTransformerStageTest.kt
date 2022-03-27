/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class InlineIfExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform inline if`() {
        driveTextFileTest(
            """
                var x = true
                var y = if (x) 1 else 0
            """.trimIndent(),
            """
                logic x = 1'b1;
                int   y = x ? 1 : 0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `transform inline if nested`() {
        driveTextFileTest(
            """
                var x = true
                var y = if (x) 1 else if (x) 2 else 3
            """.trimIndent(),
            """
                logic x = 1'b1;
                int   y = x ? 1 : x ? 2 : 3;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `transform inline if condition`() {
        driveTextFileTest(
            """
                var x = true
                @Suppress("RedundantIf")
                var y = if (if (x) true else false) 1 else 0
            """.trimIndent(),
            """
                logic x = 1'b1;
                int   y = (x ? 1'b1 : 1'b0) ? 1 : 0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
