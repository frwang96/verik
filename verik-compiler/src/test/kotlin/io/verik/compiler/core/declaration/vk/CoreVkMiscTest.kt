/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkMiscTest : CoreDeclarationTest() {

    @Test
    fun `serialize cat rep`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_cat_Any_Any,
                Core.Vk.F_rep_Any
            ),
            """
                val x = u(0x0)
                var y = u(0x00)
                fun f() {
                    y = cat(x, x)
                    y = rep<`2`>(x)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = { 4'b0000, 4'b0000 };
                    y = {2{ 4'b0000 }};
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize max`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_max_Int_Int,
                Core.Vk.F_max_Ubit_Ubit,
                Core.Vk.F_max_Sbit_Sbit,
            ),
            """
                var x = 0
                var y = u(0x0)
                var z = s(0x0)
                fun f() {
                    x = max(1, x)
                    y = max(u(0x1), y)
                    z = max(s(0x1), z)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = (1 > x) ? 1 : x;
                    y = (4'b0001 > y) ? 4'b0001 : y;
                    z = (4'sb0001 > z) ? 4'sb0001 : z;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize min`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_min_Int_Int,
                Core.Vk.F_min_Ubit_Ubit,
                Core.Vk.F_min_Sbit_Sbit,
            ),
            """
                var x = 0
                var y = u(0x0)
                var z = s(0x0)
                fun f() {
                    x = min(1, x)
                    y = min(u(0x1), y)
                    z = min(s(0x1), z)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = (1 < x) ? 1 : x;
                    y = (4'b0001 < y) ? 4'b0001 : y;
                    z = (4'sb0001 < z) ? 4'sb0001 : z;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize log exp`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_log_Int,
                Core.Vk.F_exp_Int
            ),
            """
                var x = 1
                var y = 0
                fun f() {
                    y = log(x)
                    y = exp(x)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = ${'$'}clog2(x);
                    y = 1 << x;
                endfunction : f
            """.trimIndent()
        )
    }
}
