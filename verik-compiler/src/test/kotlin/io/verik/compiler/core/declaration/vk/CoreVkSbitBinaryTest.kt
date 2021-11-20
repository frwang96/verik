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

internal class CoreVkSbitBinaryTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus mul`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_plus_Sbit,
                Core.Vk.Sbit.F_mul_Sbit
            ),
            """
                var x = s(0x0)
                var y = s(0x00)
                fun f() {
                    x = x + x
                    y = x mul x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x + x;
                    y = x * x;
                endfunction : f
            """.trimIndent()
        )
    }
}
