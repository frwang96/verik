/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class CastTransformerStageTest : BaseTest() {

    @Test
    fun `is expression`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    x = 0 is Int
                }
            """.trimIndent(),
            """
                logic x = 1'b0;

                function automatic void f();
                    int __0;
                    x = ${'$'}cast(__0, 0);
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `is not expression`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    x = 0 !is Int
                }
            """.trimIndent(),
            """
                logic x = 1'b0;

                function automatic void f();
                    int __0;
                    x = !${'$'}cast(__0, 0);
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `as expression`() {
        driveTextFileTest(
            """
                var x = 0
                fun f() {
                    x = 0 as Int
                }
            """.trimIndent(),
            """
                int x = 0;

                function automatic void f();
                    int __0;
                    if (!${'$'}cast(__0, 0)) begin
                        ${'$'}fatal(1, "Failed to cast from Int to Int");
                    end
                    x = __0;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
