/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkQueueTest : CoreDeclarationTest() {

    @Test
    fun `serialize add get size`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Queue.F_add_E,
                Core.Vk.Queue.F_size
            ),
            """
                val x: Queue<Int> = nc()
                var y = 0
                fun f() {
                    x.add(0)
                    y = x[0]
                    y = x.size()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x.push_back(0);
                    y = x[0];
                    y = x.size();
                endfunction : f
            """.trimIndent()
        )
    }
}
