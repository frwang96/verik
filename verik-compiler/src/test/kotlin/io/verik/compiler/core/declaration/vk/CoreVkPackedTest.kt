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

internal class CoreVkPackedTest : CoreDeclarationTest() {

    @Test
    fun `serialize get set size`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.Packed.F_get_Int,
                Core.Vk.Packed.F_get_Ubit,
                Core.Vk.Packed.F_set_Int_E,
                Core.Vk.Packed.F_set_Ubit_E,
                Core.Vk.Packed.P_size
            ),
            """
                var x: Packed<`2`, Boolean> = nc()
                var y = false
                var z = 0
                fun f() {
                    y = x[0]
                    y = x[u(0)]
                    x[0] = false
                    x[u(0)] = false
                    z = x.size
                }
            """.trimIndent(),
            """
                function automatic void f();
                    y = x[0];
                    y = x[1'b0];
                    x[0] = 1'b0;
                    x[1'b0] = 1'b0;
                    z = 2;
                endfunction : f
            """.trimIndent()
        )
    }
}
