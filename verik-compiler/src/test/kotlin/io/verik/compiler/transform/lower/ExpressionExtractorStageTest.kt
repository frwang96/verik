/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ExpressionExtractorStageTest : BaseTest() {

    @Test
    fun `call expression constructor`() {
        driveTextFileTest(
            """
                class C : Class()
                fun f() {
                    println(C())
                }
            """.trimIndent(),
            """
                class C;

                    function new();
                    endfunction : new

                endclass : C

                function automatic void f();
                    C __0;
                    __0 = C::new();
                    ${'$'}display(${'$'}sformatf("%p", __0));
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `constant part select expression`() {
        driveTextFileTest(
            """
                var x = u(0x00)
                var y = u(0x0)
                fun f() {
                    y = (x + x).tru()
                }
            """.trimIndent(),
            """
                logic [7:0] x = 8'h00;
                logic [3:0] y = 4'b0000;

                function automatic void f();
                    logic [7:0] __0;
                    __0 = x + x;
                    y = __0[3:0];
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `streaming expression`() {
        driveTextFileTest(
            """
                var x = u(0)
                fun f() {
                    x = x.rev() + u(0)
                }
            """.trimIndent(),
            """
                logic [0:0] x = 1'b0;

                function automatic void f();
                    logic [0:0] __0;
                    __0 = {<<{ x }};
                    x = __0 + 1'b0;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
