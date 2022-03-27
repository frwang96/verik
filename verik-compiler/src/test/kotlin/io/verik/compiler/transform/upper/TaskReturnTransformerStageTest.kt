/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TaskReturnTransformerStageTest : BaseTest() {

    @Test
    fun `internal return`() {
        driveTextFileTest(
            """
                @Task
                fun f(): Boolean { return false }
            """.trimIndent(),
            """
                task automatic f(
                    output logic __0
                );
                    __0 = 1'b0;
                    return;
                endtask : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `external return statement`() {
        driveTextFileTest(
            """
                @Task
                fun f(): Boolean { return false }
                @Task
                fun g() { f() }
            """.trimIndent(),
            """
                task automatic f(
                    output logic __0
                );
                    __0 = 1'b0;
                    return;
                endtask : f

                task automatic g();
                    logic __1;
                    f(.__0(__1));
                endtask : g
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `external return expression`() {
        driveTextFileTest(
            """
                var x = false
                @Task
                fun f(): Boolean { return false }
                @Task
                fun g() { x = f() }
            """.trimIndent(),
            """
                logic x = 1'b0;

                task automatic f(
                    output logic __0
                );
                    __0 = 1'b0;
                    return;
                endtask : f

                task automatic g();
                    logic __1;
                    f(.__0(__1));
                    x = __1;
                endtask : g
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
