/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkClassTest : CoreDeclarationTest() {

    @Test
    fun `serialize randomize`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Class.F_randomize),
            """
                class C : Class()
                val c  = C()
                fun f() {
                    c.randomize()
                }
            """.trimIndent(),
            """
                class C;

                    function new();
                    endfunction : new

                endclass : C

                function automatic void f();
                    assert (c.randomize());
                endfunction : f
            """.trimIndent()
        )
    }
}
