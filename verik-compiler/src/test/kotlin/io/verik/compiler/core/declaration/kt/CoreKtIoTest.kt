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
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreKtIoTest : CoreDeclarationTest() {

    @Test
    fun `serialize print`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Io.F_print_Any,
                Core.Kt.Io.F_print_Boolean,
                Core.Kt.Io.F_print_Int,
                Core.Kt.Io.F_print_Double
            ),
            """
                fun f() {
                    print("")
                    print(false)
                    print(0)
                    print(0.0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}write("");
                    ${'$'}write(${'$'}sformatf("%b", 1'b0));
                    ${'$'}write(${'$'}sformatf("%0d", 0));
                    ${'$'}write(${'$'}sformatf("%f", 0.0));
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize println`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Kt.Io.F_println,
                Core.Kt.Io.F_println_Any,
                Core.Kt.Io.F_println_Boolean,
                Core.Kt.Io.F_println_Int,
                Core.Kt.Io.F_println_Double
            ),
            """
                fun f() {
                    println()
                    println("")
                    println(false)
                    println(0)
                    println(0.0)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}display();
                    ${'$'}display("");
                    ${'$'}display(${'$'}sformatf("%b", 1'b0));
                    ${'$'}display(${'$'}sformatf("%0d", 0));
                    ${'$'}display(${'$'}sformatf("%f", 0.0));
                endfunction : f
            """.trimIndent()
        )
    }
}
