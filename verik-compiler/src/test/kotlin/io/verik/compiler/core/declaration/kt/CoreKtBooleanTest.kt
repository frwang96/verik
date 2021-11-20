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
import org.junit.jupiter.api.Test

internal class CoreKtBooleanTest : CoreDeclarationTest() {

    @Test
    fun `serialize not and or xor`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Boolean.F_not,
                Core.Kt.Boolean.F_and_Boolean,
                Core.Kt.Boolean.F_or_Boolean,
                Core.Kt.Boolean.F_xor_Boolean
            ),
            """
                var a = false
                var b = false
                var x = false
                fun f() {
                    x = !a
                    x = a and b
                    x = a or b
                    x = a xor b
                }
            """.trimIndent(),
            """
                function automatic void f();
                    x = !a;
                    x = a && b;
                    x = a || b;
                    x = a ^ b;
                endfunction : f
            """.trimIndent()
        )
    }
}
