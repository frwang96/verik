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

package io.verik.compiler.serialize.source

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertOutputTextEquals
import io.verik.compiler.util.driveTest
import org.junit.jupiter.api.Test

internal class ExpressionSerializerTest : BaseTest() {

    @Test
    fun `parenthesized expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
                var y = (x + 1) * x
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            int y = (x + 1) * x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `unary expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                var y = !x
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            logic y = !x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `binary expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
                var y = x + 1
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            int y = x + 1;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `reference expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
                var y = x
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            int y = x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `call expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = random()
            """.trimIndent()
        )
        val expected = """
            int x = ${"$"}random();
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `constant expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
            """.trimIndent()
        )
        val expected = """
            int x = 0;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `struct literal expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class S(val x: Boolean) : Struct()
                var s = S(false)
            """.trimIndent()
        )
        val expected = """
            typedef struct packed {
                logic x;
            } S;
            
            verik_pkg::S s = '{x:1'b0};
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `return statement`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f() {
                    return
                }
            """.trimIndent()
        )
        val expected = """
            function void f();
                return;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `injected expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
                fun f() {
                    sv("${"$"}x <= #1 !${"$"}x")
                }
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            
            function void f();
                x <= #1 !x;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `string expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = "abc"
            """.trimIndent()
        )
        val expected = """
            string x = "abc";
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `if expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                fun f() {
                    if (x) 1 else 0
                }
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            
            function void f();
                if (x)
                    1;
                else
                    0;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `if expression no then`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    if (x);
                }
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            
            function void f();
                if (x);
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `if expression nested`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                fun f() {
                    @Suppress("CascadeIf")
                    if (x) {
                        1
                    } else if (x) {
                        2
                    } else {
                        3
                        4
                    }
                }
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            
            function void f();
                if (x) begin
                    1;
                end
                else if (x) begin
                    2;
                end
                else begin
                    3;
                    4;
                end
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `inline if expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                var y = if (x) 0 else 1
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            int y   = x ? 0 : 1;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `case statement`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = 0
                fun f() {
                    when (x) {
                        0 -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
        )
        val expected = """
            int x = 0;
            
            function void f();
                case (x)
                    0 : begin
                    end
                    default : begin
                    end
                endcase
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `forever expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f() {
                    forever {}
                }
            """.trimIndent()
        )
        val expected = """
            function void f();
                forever begin
                end
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `event control expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x = false
                fun f() {
                    wait(posedge(x))
                }
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
            
            function void f();
                @(posedge x);
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `delay expression`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f() {
                    delay(1)
                }
            """.trimIndent()
        )
        val expected = """
            function void f();
                #1;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }
}
