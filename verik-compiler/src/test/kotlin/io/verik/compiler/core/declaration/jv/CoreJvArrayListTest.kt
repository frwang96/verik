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

package io.verik.compiler.core.declaration.jv

import io.verik.compiler.core.common.Core
import io.verik.compiler.util.CoreDeclarationTest
import io.verik.compiler.util.CoreDeclarationTestEntry

internal class CoreJvArrayListTest : CoreDeclarationTest() {

    override fun getEntries(): List<CoreDeclarationTestEntry> {
        return listOf(
            CoreDeclarationTestEntry(
                "ArrayList",
                listOf(
                    Core.Jv.Util.ArrayList.F_add_E,
                    Core.Jv.Util.ArrayList.F_get_Int,
                    Core.Jv.Util.ArrayList.F_set_Int_E,
                    Core.Jv.Util.ArrayList.P_size
                ),
                """
                    val a = ArrayList<Int>()
                    var x = 0
                    fun f() {
                        a.add(0)
                        x = a[0]
                        a[0] = x
                        x = a.size
                    }
                """.trimIndent(),
                """
                    function automatic void f();
                        a.add(0);
                        x = a.get(0);
                        a.set(0, x);
                        x = a.size();
                    endfunction : f
                """.trimIndent()
            )
        )
    }
}
