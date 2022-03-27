/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtBooleanTest : CoreDeclarationTest() {

    @Test
    fun `serialize not and or xor`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Boolean.F_not,
                Core.Kt.Boolean.F_and_Boolean,
                Core.Kt.Boolean.F_or_Boolean,
                Core.Kt.Boolean.F_xor_Boolean
            ),
            """
                var a = false
                var b = false
                var x = false
                fun f() {
                    x = !a
                    x = a and b
                    x = a or b
                    x = a xor b
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = !a;
                    x = a && b;
                    x = a || b;
                    x = a ^ b;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `evaluate not`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Boolean.F_not
            ),
            """
                var x = false
                fun f() {
                    @Suppress("KotlinConstantConditions")
                    x = !false
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 1'b1;
                endfunction : f
            """.trimIndent()
        )
    }
}
