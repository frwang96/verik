/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ForEachUnrollTransformerStageTest : BaseTest() {

    @Test
    fun `unroll simple`() {
        driveTextFileTest(
            """
                val x = cluster(2) { it }
                fun f() {
                    var y = 0
                    for (i in 0 until 2) {
                        y += x[i]
                    }
                }
            """.trimIndent(),
            """
                int x_0 = 0;
                int x_1 = 1;

                function automatic void f();
                    int y;
                    y = 0;
                    y = y + x_0;
                    y = y + x_1;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `unroll with property`() {
        driveTextFileTest(
            """
                val x = cluster(2) { it }
                fun f() {
                    for (i in 0 until 2) {
                        val y = x[i]
                    }
                }
            """.trimIndent(),
            """
                int x_0 = 0;
                int x_1 = 1;

                function automatic void f();
                    int y;
                    y = x_0;
                    y = x_1;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
