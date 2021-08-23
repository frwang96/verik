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

package io.verik.compiler.serialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertOutputTextEquals
import io.verik.compiler.util.driveTest
import org.junit.jupiter.api.Test

internal class DeclarationSerializerVisitorTest : BaseTest() {

    @Test
    fun `serialize module`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                class M: Module()
            """.trimIndent()
        )
        val expected = """
            module M;
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize class`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                class C
            """.trimIndent()
        )
        val expected = """
            class C;
            
            endclass : C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize enum`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                enum class E { A, B }
            """.trimIndent()
        )
        val expected = """
            typedef enum {
                A,
                B
            } E;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize function`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                fun f() {}
            """.trimIndent()
        )
        val expected = """
            function void f();
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize property`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                var x = false
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize initial block`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                class C {
                    @Run
                    fun f() {}
                }
            """.trimIndent()
        )
        val expected = """
            class C;
            
                initial begin : f
                end : f
            
            endclass : C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize always com block`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                class C {
                    @Com
                    fun f() {}
                }
            """.trimIndent()
        )
        val expected = """
            class C;
            
                always_comb begin : f
                end : f
            
            endclass : C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize always seq block`() {
        val projectContext = driveTest(
            SourceSerializer::class,
            """
                class C {
                    private var x = false
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent()
        )
        val expected = """
            class C;
            
                logic x = 1'b0;
            
                always_ff @(posedge x) begin : f
                end : f
            
            endclass : C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }
}
