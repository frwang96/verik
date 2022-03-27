/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtIoTest : CoreDeclarationTest() {

    @Test
    fun `serialize print`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Io.F_print_Any,
                Core.Kt.Io.F_print_Boolean,
                Core.Kt.Io.F_print_Int,
                Core.Kt.Io.F_print_Double
            ),
            """
                fun f() {
                    print("")
                    print(false)
                    print(0)
                    print(0.0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}write("");
                    ${'$'}write(${'$'}sformatf("%b", 1'b0));
                    ${'$'}write(${'$'}sformatf("%0d", 0));
                    ${'$'}write(${'$'}sformatf("%f", 0.0));
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize println`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Io.F_println,
                Core.Kt.Io.F_println_Any,
                Core.Kt.Io.F_println_Boolean,
                Core.Kt.Io.F_println_Int,
                Core.Kt.Io.F_println_Double
            ),
            """
                fun f() {
                    println()
                    println("")
                    println(false)
                    println(0)
                    println(0.0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}display();
                    ${'$'}display("");
                    ${'$'}display(${'$'}sformatf("%b", 1'b0));
                    ${'$'}display(${'$'}sformatf("%0d", 0));
                    ${'$'}display(${'$'}sformatf("%f", 0.0));
                endfunction : f
            """.trimIndent()
        )
    }
}
