/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtStringTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus`() {
        driveCoreDeclarationTest(
            listOf(Core.Kt.String.F_plus_Any),
            """
                var s = ""
                fun f() {
                    s = "a" + "b"
                    s = "a" + "b" + "c"
                }
            """.trimIndent(),
            """
                function automatic void f();
                    s = { "a", "b" };
                    s = { "a", "b", "c" };
                endfunction : f
            """.trimIndent()
        )
    }
}
