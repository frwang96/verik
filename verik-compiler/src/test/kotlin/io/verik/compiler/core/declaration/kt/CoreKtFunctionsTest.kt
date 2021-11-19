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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.util.CoreDeclarationTest
import io.verik.compiler.util.CoreDeclarationTestEntry

internal class CoreKtFunctionsTest : CoreDeclarationTest() {

    override fun getEntries(): List<CoreDeclarationTestEntry> {
        return listOf(
            CoreDeclarationTestEntry(
                "repeat assert",
                listOf(
                    Core.Kt.F_repeat_Int_Function,
                    Core.Kt.F_assert_Boolean,
                    Core.Kt.F_assert_Boolean_Function
                ),
                """
                    fun f() {
                        repeat(1) {}
                        assert(true)
                        assert(true) {}
                    }
                """.trimIndent(),
                """
                    function automatic void f();
                        repeat (1) begin
                        end
                        assert (1'b1);
                        assert (1'b1) else begin
                        end
                    endfunction : f
                """.trimIndent()
            )
        )
    }
}
