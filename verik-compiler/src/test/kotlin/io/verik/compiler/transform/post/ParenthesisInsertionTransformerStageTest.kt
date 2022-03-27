/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ParenthesisInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `unary expression with unary expression`() {
        driveTextFileTest(
            """
                var x = !u(0x0).orRed()
            """.trimIndent(),
            """
                logic x = !(|4'b0000);
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `unary expression with binary expression`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                var z = !(x && y)
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                logic z = !(x && y);
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `binary expression same kind left`() {
        driveTextFileTest(
            """
                var x = 0
                var y = (x + 1) + x
            """.trimIndent(),
            """
                int x = 0;
                int y = x + 1 + x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `binary expression same kind right`() {
        driveTextFileTest(
            """
                var x = 0
                var y = x + (1 + x)
            """.trimIndent(),
            """
                int x = 0;
                int y = x + (1 + x);
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `binary expression different kind`() {
        driveTextFileTest(
            """
                var x = 0
                var y = (x * 2) + x
            """.trimIndent(),
            """
                int x = 0;
                int y = (x * 2) + x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression same kind left`() {
        driveTextFileTest(
            """
                var x = false
                @Suppress("RedundantIf")
                var y = if (if (x) true else false) 3 else 4
            """.trimIndent(),
            """
                logic x = 1'b0;
                int   y = (x ? 1'b1 : 1'b0) ? 3 : 4;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression same kind right`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                var z = if (x) 1 else if (y) 2 else 3
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                int   z = x ? 1 : y ? 2 : 3;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression different kind`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                var z = if (x && y) 1 else 0
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                int   z = (x && y) ? 1 : 0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
