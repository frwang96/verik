/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkSystemTest : CoreDeclarationTest() {

    @Test
    fun `serialize strobe monitor`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_strobe_String,
                Core.Vk.F_monitor_String
            ),
            """
                fun f() {
                    strobe("")
                    monitor("")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}strobe("");
                    ${'$'}monitor("");
                endfunction : f
            """.trimIndent()
        )
    }

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
    fun `serialize error warning info`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_error_String),
            """
                fun f() {
                    error("")
                    warning("")
                    info("")
                }
            """.trimIndent(),
            """
                function automatic void f();
                    ${'$'}error("");
                    ${'$'}warning("");
                    ${'$'}info("");
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
