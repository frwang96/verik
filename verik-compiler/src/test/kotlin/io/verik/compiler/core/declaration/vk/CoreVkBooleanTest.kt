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

internal class CoreVkBooleanTest : CoreDeclarationTest() {

    @Test
    fun `serialize toUbit toSbit`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Boolean.F_Boolean_toUbit,
                Core.Vk.Boolean.F_Boolean_toSbit
            ),
            """
                var a = false
                var x0 = u(0x0)
                var x1 = u(0b0)
                var x2 = s(0x0)
                var x3 = s(0b0)
                fun f() {
                    x0 = a.toUbit()
                    x1 = a.toUbit()
                    x2 = a.toSbit()
                    x3 = a.toSbit()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x0 = 4'(a);
                    x1 = a;
                    x2 = 4'(${'$'}signed(a));
                    x3 = ${'$'}signed(a);
                endfunction : f
            """.trimIndent()
        )
    }
}
