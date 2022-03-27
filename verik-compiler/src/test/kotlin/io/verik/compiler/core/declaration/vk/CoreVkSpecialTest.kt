/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkSpecialTest : CoreDeclarationTest() {

    @Test
    fun `serialize inj inji t`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_inj_String,
                Core.Vk.F_inji_String,
                Core.Vk.F_t
            ),
            """
                var x = false
                fun f() {
                    inj("${'$'}{t<Int>()} x;")
                    x = inji("1'b1")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    int x;
                    x = 1'b1;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize nc illegal`() {
        driveMessageTest(
            """
                var x = false
                fun f() {
                    x = nc()
                }
            """.trimIndent(),
            true,
            "Expression used out of context: nc"
        )
    }
}
