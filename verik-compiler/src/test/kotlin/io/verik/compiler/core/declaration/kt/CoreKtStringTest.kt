/*
 * Copyright (c) 2022 Francis Wang
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
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtStringTest : CoreDeclarationTest() {

    @Test
    fun `serialize plus`() {
        driveCoreDeclarationTest(
            listOf(Core.Kt.String.F_plus_Any),
            """
                var s = ""
                fun f() {
                    s = "a" + "b"
                    s = "a" + "b" + "c"
                }
            """.trimIndent(),
            """
                function automatic void f();
                    s = { "a", "b" };
                    s = { "a", "b", "c" };
                endfunction : f
            """.trimIndent()
        )
    }
}
