/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkMailboxTest : CoreDeclarationTest() {

    @Test
    fun `serialize new put get`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Mailbox.F_put_T,
                Core.Vk.Mailbox.F_get
            ),
            """
                var x: Mailbox<Int> = nc()
                var y = 0
                fun f() {
                    x = Mailbox()
                    x.put(0)
                    y = x.get()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    int __0;
                    x = new();
                    x.put(0);
                    x.get(__0);
                    y = __0;
                endfunction : f
            """.trimIndent()
        )
    }
}
