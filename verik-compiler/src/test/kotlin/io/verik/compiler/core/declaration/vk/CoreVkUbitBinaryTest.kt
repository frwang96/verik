/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkUbitBinaryTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus add minus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_plus_Ubit,
                Core.Vk.Ubit.F_plus_Sbit,
                Core.Vk.Ubit.F_add_Ubit,
                Core.Vk.Ubit.F_add_Sbit,
                Core.Vk.Ubit.F_minus_Ubit,
                Core.Vk.Ubit.F_minus_Sbit
            ),
            """
                var x = u(0b0)
                var y = u(0b00)
                fun f() {
                    x = x + u(0b0)
                    x = x + s(0b0)
                    y = x add u(0b0)
                    y = x add s(0b0)
                    x = x - u(0b0)
                    x = x - s(0b0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x + 1'h0;
                    x = x + 1'sh0;
                    y = x + 1'h0;
                    y = x + 1'sh0;
                    x = x - 1'h0;
                    x = x - 1'sh0;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize times mul div`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_times_Ubit,
                Core.Vk.Ubit.F_times_Sbit,
                Core.Vk.Ubit.F_mul_Ubit,
                Core.Vk.Ubit.F_mul_Sbit,
                Core.Vk.Ubit.F_div_Ubit
            ),
            """
                var x = u(0x0)
                var y = u(0x00)
                fun f() {
                    x = x * u(0x0)
                    x = x * s(0x0)
                    y = x mul u(0x0)
                    y = x mul s(0x0)
                    x = x / x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x * 4'h0;
                    x = x * 4'sh0;
                    y = x * 4'h0;
                    y = x * 4'sh0;
                    x = x / x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize and or xor`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_and_Ubit,
                Core.Vk.Ubit.F_and_Sbit,
                Core.Vk.Ubit.F_or_Ubit,
                Core.Vk.Ubit.F_or_Sbit,
                Core.Vk.Ubit.F_xor_Ubit,
                Core.Vk.Ubit.F_xor_Sbit
            ),
            """
                var x = u(0x0)
                var y = s(0x0)
                fun f() {
                    x = x and x
                    x = x and y
                    x = x or x
                    x = x or y
                    x = x xor x
                    x = x xor y
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x & x;
                    x = x & y;
                    x = x | x;
                    x = x | y;
                    x = x ^ x;
                    x = x ^ y;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize shl shr sshr`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_shl_Int,
                Core.Vk.Ubit.F_shl_Ubit,
                Core.Vk.Ubit.F_shr_Int,
                Core.Vk.Ubit.F_shr_Ubit,
                Core.Vk.Ubit.F_sshr_Int,
                Core.Vk.Ubit.F_sshr_Ubit
            ),
            """
                var x = u(0x0)
                fun f() {
                    x = x shl 1
                    x = x shl u(1)
                    x = x shr 1
                    x = x shr u(1)
                    x = x sshr 1
                    x = x sshr u(1)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x << 1;
                    x = x << 1'h1;
                    x = x >> 1;
                    x = x >> 1'h1;
                    x = ${'$'}unsigned(${'$'}signed(x) >>> 1);
                    x = ${'$'}unsigned(${'$'}signed(x) >>> 1'h1);
                endfunction : f
            """.trimIndent()
        )
    }
}
