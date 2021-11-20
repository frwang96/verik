/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.util.CoreDeclarationTest
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
                    x = random(2, 3)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = ${'$'}random();
                    x = ${'$'}urandom_range(3);
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
