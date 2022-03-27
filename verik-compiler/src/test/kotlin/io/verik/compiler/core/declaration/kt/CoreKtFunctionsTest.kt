/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtFunctionsTest : CoreDeclarationTest() {

    @Test
    fun `serialize repeat assert`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.F_repeat_Int_Function,
                Core.Kt.F_assert_Boolean,
                Core.Kt.F_assert_Boolean_Function
            ),
            """
                fun f() {
                    repeat(1) {}
                    assert(true)
                    assert(true) {}
                }
            """.trimIndent(),
            """
                function automatic void f();
                    repeat (1) begin
                    end
                    assert (1'b1);
                    assert (1'b1) else begin
                    end
                endfunction : f
            """.trimIndent()
        )
    }
}
