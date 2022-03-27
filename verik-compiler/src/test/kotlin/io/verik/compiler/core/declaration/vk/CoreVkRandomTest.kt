/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkRandomTest : CoreDeclarationTest() {

    @Test
    fun `serialize random`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_random,
                Core.Vk.F_random_Int,
                Core.Vk.F_random_Int_Int
            ),
            """
                var x = 0
                fun f() {
                    x = random()
                    x = random(3)
                    x = random(2, 4)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = ${'$'}random();
                    x = ${'$'}urandom_range(2);
                    x = ${'$'}urandom_range(2, 3);
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize randomBoolean randomInt`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_randomBoolean,
                Core.Vk.F_randomUbit
            ),
            """
                var x = false
                var y = u(0x0)
                fun f() {
                    x = randomBoolean()
                    y = randomUbit()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = ${'$'}urandom();
                    y = ${'$'}urandom();
                endfunction : f
            """.trimIndent()
        )
    }
}
