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

internal class CoreVkUbitTest : CoreDeclarationTest() {

    @Test
    fun `serialize unaryPlus unaryMinus`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_unaryPlus,
                Core.Vk.Ubit.F_unaryMinus
            ),
            """
                var x = u(0x0)
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
                Core.Vk.Ubit.F_get_Int,
                Core.Vk.Ubit.F_get_Ubit,
                Core.Vk.Ubit.F_set_Int_Boolean,
                Core.Vk.Ubit.F_set_Ubit_Boolean,
                Core.Vk.Ubit.F_set_Int_Ubit,
                Core.Vk.Ubit.F_set_Ubit_Ubit
            ),
            """
                var x = u(0x0)
                var y = false
                fun f() {
                    y = x[0]
                    y = x[u(0b00)]
                    x[0] = y
                    x[u(0b00)] = y
                    x[0] = u(0b00)
                    x[u(0b00)] = u(0b00)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[0];
                    y = x[2'b00];
                    x[0] = y;
                    x[2'b00] = y;
                    x[1:0] = 2'b00;
                    x[2'b01:2'b00] = 2'b00;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize not`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_not
            ),
            """
                var x = u(0x0)
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
    fun `serialize inv rev`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_inv,
                Core.Vk.Ubit.F_rev
            ),
            """
                var x = u(0x0)
                fun f() {
                    x = x.inv()
                    x = x.rev()
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
    fun `serialize andRed orRed xorRed`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_andRed,
                Core.Vk.Ubit.F_orRed,
                Core.Vk.Ubit.F_xorRed
            ),
            """
                var x = u(0x00)
                var y = false
                fun f() {
                    y = x.andRed()
                    y = x.orRed()
                    y = x.xorRed()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = &x;
                    y = |x;
                    y = ^x;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `eqz neqz`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_eqz,
                Core.Vk.Ubit.F_neqz
            ),
            """
                var x = u(0x00)
                var y = false
                fun f() {
                    y = x.eqz()
                    y = x.neqz()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x == 8'h00;
                    y = x != 8'h00;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize sli`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_sli,
                Core.Vk.Ubit.F_sli_Int,
                Core.Vk.Ubit.F_sli_Ubit
            ),
            """
                var x = u(0x00)
                var y = u(0x0)
                fun f() {
                    y = x.sli<`4`, `0`>()
                    y = x.sli(0)
                    y = x.sli(u(0b000))
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[3:0];
                    y = x[3:0];
                    y = x[3'b011:3'b000];
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
                fun f() {
                    y = x.ext()
                    y = x.sext()
                    x = y.tru()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = 8'(x);
                    y = ${'$'}unsigned(8'(${'$'}signed(x)));
                    x = y[3:0];
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize res sres`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_res,
                Core.Vk.Ubit.F_sres
            ),
            """
                var x = u(0x0)
                var y = u(0x00)
                fun f() {
                    y = x.res()
                    x = y.res()
                    y = x.sres()
                    x = y.sres()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = 8'(x);
                    x = y[3:0];
                    y = ${'$'}unsigned(8'(${'$'}signed(x)));
                    x = y[3:0];
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize toSbit`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.Ubit.F_toSbit),
            """
                var x = u(0x0)
                var y = s(0x0)
                fun f() {
                    y = x.toSbit()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = ${'$'}signed(x);
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize toBinString toDecString toHexString`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Ubit.F_toBinString,
                Core.Vk.Ubit.F_toDecString,
                Core.Vk.Ubit.F_toHexString
            ),
            """
                var x = u(0x0)
                var y = ""
                fun f() {
                    y = x.toBinString()
                    y = x.toDecString()
                    y = x.toHexString()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = ${'$'}sformatf("%b", x);
                    y = ${'$'}sformatf("%0d", x);
                    y = ${'$'}sformatf("%h", x);
                endfunction : f
            """.trimIndent()
        )
    }
}
