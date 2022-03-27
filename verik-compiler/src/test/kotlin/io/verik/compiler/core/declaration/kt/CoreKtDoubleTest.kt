/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtDoubleTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus div`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Double.F_plus_Int,
                Core.Kt.Double.F_plus_Double,
                Core.Kt.Double.F_div_Int
            ),
            """
                var x = 0.0
                fun f() {
                    x += 1
                    x += 0.1
                    x /= 1

                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x + 1;
                    x = x + 0.1;
                    x = x / 1;
                endfunction : f
            """.trimIndent()
        )
    }
}
