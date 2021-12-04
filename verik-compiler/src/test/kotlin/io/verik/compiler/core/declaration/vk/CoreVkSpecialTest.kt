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

internal class CoreVkSpecialTest : CoreDeclarationTest() {

    @Test
    fun `serialize sv`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_sv_String),
            """
                fun f() {
                    sv("xyz")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    xyz;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `transform nc illegal`() {
        driveMessageTest(
            """
                var x = false
                fun f() {
                    x = nc()
                }
            """.trimIndent(),
            true,
            "Expression used out of context: nc"
        )
    }

    @Test
    fun `serialize i`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_i),
            """
                var x = 0
                fun f() {
                    x = i<`8`>()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 8;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize u u0`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_u,
                Core.Vk.F_u_Boolean,
                Core.Vk.F_u_Sbit,
                Core.Vk.F_u0
            ),
            """
                var x = u(0x0)
                var y = u(0b0)
                var z = s(0x0)
                fun f() {
                    x = u<`8`>()
                    y = u(false)
                    x = u(z)
                    x = u0()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = 4'h8;
                    y = 1'b0;
                    x = ${'$'}unsigned(z);
                    x = 4'h0;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize s`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_s_Ubit),
            """
                var x = s(0x0)
                var y = u(0x0)
                fun f() {
                    x = s(y)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = ${'$'}signed(y);
                endfunction : f
            """.trimIndent()
        )
    }
}
