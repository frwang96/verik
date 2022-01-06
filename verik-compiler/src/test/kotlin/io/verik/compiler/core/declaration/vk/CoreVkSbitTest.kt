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

internal class CoreVkSbitTest : CoreDeclarationTest() {

    @Test
    fun `serialize unaryPlus unaryMinus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_unaryPlus,
                Core.Vk.Sbit.F_unaryMinus
            ),
            """
                var x = s(0x0)
                fun f() {
                    x = +x
                    x = -x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = x;
                    x = -x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize get set`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_get_Int,
                Core.Vk.Sbit.F_get_Ubit,
                Core.Vk.Sbit.F_set_Int_Boolean,
                Core.Vk.Sbit.F_set_Ubit_Boolean
            ),
            """
                var x = s(0x0)
                var y = false
                fun f() {
                    y = x[0]
                    y = x[u(0b00)]
                    x[0] = y
                    x[u(0b00)] = y
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[0];
                    y = x[2'b00];
                    x[0] = y;
                    x[2'b00] = y;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize not`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_not
            ),
            """
                var x = s(0x0)
                var y = false
                fun f() {
                    y = !x
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = !x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize ext sext tru`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Sbit.F_ext,
                Core.Vk.Sbit.F_uext,
                Core.Vk.Sbit.F_tru
            ),
            """
                var x = s(0x0)
                var y = s(0x00)
                fun f() {
                    y = x.ext()
                    y = x.uext()
                    x = y.tru()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = 8'(x);
                    y = ${'$'}signed(8'(${'$'}unsigned(x)));
                    x = 4'(y);
                endfunction : f
            """.trimIndent()
        )
    }
}
