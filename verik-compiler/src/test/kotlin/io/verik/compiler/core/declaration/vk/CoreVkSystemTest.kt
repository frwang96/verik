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

internal class CoreVkSystemTest : CoreDeclarationTest() {

    @Test
    fun `serialize finish fatal`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_finish,
                Core.Vk.F_fatal,
                Core.Vk.F_fatal_String
            ),
            """
                fun f() { finish() }
                fun g() { fatal() }
                fun h() { fatal("") }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}finish();
                endfunction : f

                function automatic void g();
                    ${'$'}fatal();
                endfunction : g

                function automatic void h();
                    ${'$'}fatal(1, "");
                endfunction : h
            """.trimIndent()
        )
    }

    @Test
    fun `serialize error`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_error_String),
            """
                fun f() {
                    error("")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}error("");
                endfunction : f
            """.trimIndent()
        )
    }

    @Test
    fun `serialize time`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_time),
            """
                var t = time()
                fun f() {
                    t = time()
                }
            """.trimIndent(),
            """
                function automatic void f();
                    t = ${'$'}time();
                endfunction : f
            """.trimIndent()
        )
    }
}
