/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkSbitTest : CoreDeclarationTest() {

    @Test
    fun `serialize unaryPlus unaryMinus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_unaryPlus,
                Core.Vk.Sbit.F_unaryMinus
            ),
            """
                var x = s(0x0)
                fun f() {
                    x = +x
                    x = -x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x;
                    x = -x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize get set`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_get_Int,
                Core.Vk.Sbit.F_get_Ubit,
                Core.Vk.Sbit.F_set_Int_Boolean,
                Core.Vk.Sbit.F_set_Ubit_Boolean
            ),
            """
                var x = s(0x0)
                var y = false
                fun f() {
                    y = x[0]
                    y = x[u(0b00)]
                    x[0] = y
                    x[u(0b00)] = y
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[0];
                    y = x[2'b00];
                    x[0] = y;
                    x[2'b00] = y;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize not`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_not
            ),
            """
                var x = s(0x0)
                var y = false
                fun f() {
                    y = !x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = !x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize ext sext tru`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_ext,
                Core.Vk.Sbit.F_uext,
                Core.Vk.Sbit.F_tru
            ),
            """
                var x = s(0x0)
                var y = s(0x00)
                fun f() {
                    y = x.ext()
                    y = x.uext()
                    x = y.tru()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = 8'(x);
                    y = ${'$'}signed(8'(${'$'}unsigned(x)));
                    x = 4'(y);
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize toUbit`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Sbit.F_toUbit),
            """
                var x = s(0x0)
                var y = u(0x0)
                fun f() {
                    y = x.toUbit()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = ${'$'}unsigned(x);
                endfunction : f
            """.trimIndent()
        )
    }
}
