/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class PropertyStatementReordererStageTest : BaseTest() {

    @Test
    fun `reorder property statement with initializer`() {
        driveTextFileTest(
            """
                fun f() {
                    println()
                    var x = false
                }
            """.trimIndent(),
            """
                function automatic void f();
                    logic x;
                    ${'$'}display();
                    x = 1'b0;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `reorder property statement without initializer`() {
        driveTextFileTest(
            """
                fun f() {
                    println()
                    var x: Boolean = nc()
                    x = false
                }
            """.trimIndent(),
            """
                function automatic void f();
                    logic x;
                    ${'$'}display();
                    x = 1'b0;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
