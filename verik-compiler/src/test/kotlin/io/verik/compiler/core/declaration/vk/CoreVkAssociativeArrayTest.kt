/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkAssociativeArrayTest : CoreDeclarationTest() {

    @Test
    fun `serialize set get`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.AssociativeArray.F_set_K_V,
                Core.Vk.AssociativeArray.F_get_K
            ),
            """
                val x: AssociativeArray<Int, Int> = nc()
                var y = 0
                fun f() {
                    x[0] = 1
                    y = x[0]
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x[0] = 1;
                    y = x[0];
                endfunction : f
            """.trimIndent()
        )
    }
}
