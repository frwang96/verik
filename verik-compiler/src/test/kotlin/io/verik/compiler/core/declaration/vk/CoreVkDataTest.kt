/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkDataTest : CoreDeclarationTest() {

    @Test
    fun `serialize b i`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_i),
            """
                var x = false
                var y = 0
                fun f() {
                    x = b<TRUE>()
                    y = i<`8`>()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 1'b1;
                    y = 8;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize u u0 u1`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_u,
                Core.Vk.F_u0,
                Core.Vk.F_u1,
                Core.Vk.F_ux,
                Core.Vk.F_uz
            ),
            """
                var x = u(0x0)
                fun f() {
                    x = u<`8`>()
                    x = u0()
                    x = u1()
                    x = ux()
                    x = uz()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 4'b1000;
                    x = 4'b0000;
                    x = 4'b1111;
                    x = 4'bx;
                    x = 4'bz;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize s0 s1`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_s0,
                Core.Vk.F_s1
            ),
            """
                var x = s(0x0)
                fun f() {
                    x = s0()
                    x = s1()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 4'sb0000;
                    x = 4'sb1111;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize fill0 fill1 fillx fillz`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_fill0,
                Core.Vk.F_fill1,
                Core.Vk.F_fillx,
                Core.Vk.F_fillz
            ),
            """
                var x: Packed<`8`, Ubit<`4`>> = nc()
                fun f() {
                    x = fill0()
                    x = fill1()
                    x = fillx()
                    x = fillz()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = '{default:'0};
                    x = '{default:'1};
                    x = '{default:'x};
                    x = '{default:'z};
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize unknown floating`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.P_unknown,
                Core.Vk.P_floating
            ),
            """
                var x = false
                fun f() {
                    x = unknown
                    x = floating
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 1'bx;
                    x = 1'bz;
                endfunction : f
            """.trimIndent()
        )
    }
}
