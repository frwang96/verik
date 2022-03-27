/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkEventTest : CoreDeclarationTest() {

    @Test
    fun `serialize trigger`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Event.F_trigger),
            """
                var e: Event = nc()
                fun f() {
                    e.trigger()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ->e;
                endfunction : f
            """.trimIndent()
        )
    }
}
