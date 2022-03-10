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
    fun `serialize inj inji t`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_inj_String,
                Core.Vk.F_inji_String,
                Core.Vk.F_t
            ),
            """
                var x = false
                fun f() {
                    inj("${'$'}{t<Int>()} x;")
                    x = inji("1'b1")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    int x;
                    x = 1'b1;
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize nc illegal`() {
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
}
