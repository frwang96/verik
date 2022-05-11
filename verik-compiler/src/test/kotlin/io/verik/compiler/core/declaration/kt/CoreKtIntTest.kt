/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtIntTest : CoreDeclarationTest() {

    @Test
    fun `serialize unaryPlus unaryMinus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Int.F_unaryPlus,
                Core.Kt.Int.F_unaryMinus
            ),
            """
                var a = 0
                var x = 0
                fun f() {
                    x = +a
                    x = -a
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = a;
                    x = -a;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize plus minus times div`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Int.F_plus_Int,
                Core.Kt.Int.F_minus_Int,
                Core.Kt.Int.F_times_Int,
                Core.Kt.Int.F_div_Int
            ),
            """
                var a = 0
                var b = 0
                var x = 0
                fun f() {
                    x = a + b
                    x = a - b
                    x = a * b
                    x = a / b
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = a + b;
                    x = a - b;
                    x = a * b;
                    x = a / b;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `evaluate plus minus times`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Int.F_plus_Int,
                Core.Kt.Int.F_minus_Int,
                Core.Kt.Int.F_times_Int
            ),
            """
                var x = 0
                fun f() {
                    x = 1 + 1
                    x = 2 - 1
                    x = 1 * 2
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 2;
                    x = 1;
                    x = 2;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize shl shr ushr`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Int.F_shl_Int,
                Core.Kt.Int.F_shr_Int,
                Core.Kt.Int.F_ushr_Int
            ),
            """
                var a = 0
                var b = 0
                var x = 0
                fun f() {
                    x = a shl b
                    x = a shr b
                    x = a ushr b
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = a << b;
                    x = a >>> b;
                    x = a >> b;
                endfunction : f
            """.trimIndent()
        )
    }
}
