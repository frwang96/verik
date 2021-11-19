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
import io.verik.compiler.util.CoreDeclarationTestEntry

internal class CoreVkBooleanTest : CoreDeclarationTest() {

    override fun getEntries(): List<CoreDeclarationTestEntry> {
        return listOf(
            CoreDeclarationTestEntry(
                "Boolean",
                listOf(
                    Core.Vk.Boolean.F_Boolean_ext,
                    Core.Vk.Boolean.F_Boolean_sext
                ),
                """
                    var a = false
                    var x = u(0x0)
                    var y = s(0x0)
                    fun f() {
                        x = a.ext()
                        y = a.sext()
                    }
                """.trimIndent(),
                """
                    function automatic void f();
                        x = 4'(a);
                        y = 4'(${'$'}signed(a));
                    endfunction : f
                """.trimIndent()
            )
        )
    }
}
