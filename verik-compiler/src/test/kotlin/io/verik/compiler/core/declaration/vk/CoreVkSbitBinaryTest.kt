/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkSbitBinaryTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus add minus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_plus_Ubit,
                Core.Vk.Sbit.F_plus_Sbit,
                Core.Vk.Sbit.F_add_Ubit,
                Core.Vk.Sbit.F_add_Sbit,
                Core.Vk.Sbit.F_minus_Ubit,
                Core.Vk.Sbit.F_minus_Sbit
            ),
            """
                var x = s(0b0)
                var y = u(0b00)
                var z = s(0b00)
                fun f() {
                    y = x + u(0b00)
                    z = x + s(0b00)
                    y = x add u(0b0)
                    z = x add s(0b0)
                    y = x - u(0b00)
                    z = x - s(0b00)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x + 2'b00;
                    z = x + 2'sb00;
                    y = x + 1'b0;
                    z = x + 1'sb0;
                    y = x - 2'b00;
                    z = x - 2'sb00;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize times mul`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_times_Ubit,
                Core.Vk.Sbit.F_times_Sbit,
                Core.Vk.Sbit.F_mul_Ubit,
                Core.Vk.Sbit.F_mul_Sbit,
            ),
            """
                var x = s(0x0)
                var y = u(0x00)
                var z = s(0x00)
                fun f() {
                    y = x * u(0x00)
                    z = x * s(0x00)
                    y = x mul u(0x0)
                    z = x mul s(0x0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x * 8'h00;
                    z = x * 8'sh00;
                    y = x * 4'b0000;
                    z = x * 4'sb0000;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize and or xor`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_and_Ubit,
                Core.Vk.Sbit.F_and_Sbit,
                Core.Vk.Sbit.F_or_Ubit,
                Core.Vk.Sbit.F_or_Sbit,
                Core.Vk.Sbit.F_xor_Ubit,
                Core.Vk.Sbit.F_xor_Sbit
            ),
            """
                var x = s(0x0)
                var y = u(0x0)
                fun f() {
                    y = x and x
                    y = x and y
                    y = x or x
                    y = x or y
                    y = x xor x
                    y = x xor y
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x & x;
                    y = x & y;
                    y = x | x;
                    y = x | y;
                    y = x ^ x;
                    y = x ^ y;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize shl shr ushr`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_shl_Int,
                Core.Vk.Sbit.F_shl_Ubit,
                Core.Vk.Sbit.F_shr_Int,
                Core.Vk.Sbit.F_shr_Ubit,
                Core.Vk.Sbit.F_ushr_Int,
                Core.Vk.Sbit.F_ushr_Ubit
            ),
            """
                var x = s(0x0)
                fun f() {
                    x = x shl 1
                    x = x shl u(1)
                    x = x shr 1
                    x = x shr u(1)
                    x = x ushr 1
                    x = x ushr u(1)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x << 1;
                    x = x << 1'b1;
                    x = x >>> 1;
                    x = x >>> 1'b1;
                    x = x >> 1;
                    x = x >> 1'b1;
                endfunction : f
            """.trimIndent()
        )
    }
}
