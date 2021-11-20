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

internal class CoreVkUbitTest : CoreDeclarationTest() {

    @Test
    fun `serialize unaryMinus`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Ubit.F_unaryMinus),
            """
                var x = u(0x0)
                fun f() {
                    x = -x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = -x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize get set`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_get_Int,
                Core.Vk.Ubit.F_set_Int_Boolean,
                Core.Vk.Ubit.F_set_Int_Ubit
            ),
            """
                var x = u(0x0)
                var y = false
                fun f() {
                    y = x[0]
                    x[0] = y
                    x[0] = u(0b00)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[0];
                    x[0] = y;
                    x[1:0] = 2'h0;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize invert reverse`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_invert,
                Core.Vk.Ubit.F_reverse
            ),
            """
                var x = u(0x0)
                fun f() {
                    x = x.invert()
                    x = x.reverse()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = ~x;
                    x = {<<{ x }};
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize slice`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Ubit.F_slice_Int),
            """
                var x = u(0x00)
                var y = u(0x0)
                fun f() {
                    y = x.slice(0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[3:0];
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize ext sext tru`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_ext,
                Core.Vk.Ubit.F_sext,
                Core.Vk.Ubit.F_tru
            ),
            """
                var x = u(0x0)
                var y = u(0x00)
                var z = s(0x00)
                fun f() {
                    y = x.ext()
                    z = x.sext()
                    x = y.tru()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = 8'(x);
                    z = 8'(${'$'}signed(x));
                    x = 4'(y);
                endfunction : f
            """.trimIndent()
        )
    }
}
