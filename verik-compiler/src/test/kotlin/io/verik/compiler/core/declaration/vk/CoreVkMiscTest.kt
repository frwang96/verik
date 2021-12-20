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
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkMiscTest : CoreDeclarationTest() {

    @Test
    fun `serialize cat rep`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_cat_Any_Any,
                Core.Vk.F_rep_Any
            ),
            """
                val x = u(0x0)
                var y = u(0x00)
                fun f() {
                    y = cat(x, x)
                    y = rep<`2`>(x)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = { 4'h0, 4'h0 };
                    y = {2{ 4'h0 }};
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize log exp`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_log_Int,
                Core.Vk.F_exp_Int
            ),
            """
                var x = 1
                var y = 0
                fun f() {
                    y = log(x)
                    y = exp(x)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = ${'$'}clog2(x);
                    y = 1 << x;
                endfunction : f
            """.trimIndent()
        )
    }
}
