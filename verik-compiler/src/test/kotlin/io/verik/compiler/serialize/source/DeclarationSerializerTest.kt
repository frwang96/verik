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
import org.junit.jupiter.api.Test

internal class DeclarationSerializerTest : BaseTest() {

    @Test
    fun `serialize type definition`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f(x: Unpacked<`8`, Boolean>): Unpacked<`8`, Boolean> {
                    return x
                }
            """.trimIndent()
        )
        val expected = """
            typedef logic _${'$'}f [7:0];
            
            function automatic _${'$'}f f(logic x [7:0]);
                return x;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize module simple`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
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
    fun `serialize module with port`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent()
        )
        val expected = """
            module M(
                input logic x
            );
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize module interface simple`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class MI: ModuleInterface()
            """.trimIndent()
        )
        val expected = """
            interface MI;
            
            endinterface : MI
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize class`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class C
            """.trimIndent()
        )
        val expected = """
            class C;
            
                static function automatic verik_pkg::C vknew();
                    automatic verik_pkg::C _${'$'}0 = new();
                    return _${'$'}0;
                endfunction : vknew
            
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
            SourceSerializerStage::class,
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
    fun `serialize struct`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class S(val x: Boolean) : Struct()
            """.trimIndent()
        )
        val expected = """
            typedef struct packed {
                logic x;
            } S;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize function`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f() {}
            """.trimIndent()
        )
        val expected = """
            function automatic void f();
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize task`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                @Task
                fun t(x: Int) {}
            """.trimIndent()
        )
        val expected = """
            task automatic t(int x);
            endtask : t
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize property`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
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
            SourceSerializerStage::class,
            """
                class M : Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent()
        )
        val expected = """
            module M;
            
                initial begin : f
                end : f
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize always com block`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class M : Module() {
                    @Com
                    fun f() {}
                }
            """.trimIndent()
        )
        val expected = """
            module M;
            
                always_comb begin : f
                end : f
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize always seq block`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class M : Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent()
        )
        val expected = """
            module M;
            
                logic x = 1'b0;
            
                always_ff @(posedge x) begin : f
                end : f
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize module instantiation`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class M(@In var x: Boolean): Module()
                class Top : Module() {
                    @Make
                    val m = M(false)
                }
            """.trimIndent()
        )
        val expected = """
            module M(
                input logic x
            );
            
            endmodule : M
            
            module Top;
            
                M m (
                    .x ( 1'b0 )
                );
            
            endmodule : Top
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize module port instantiation`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class MP(@In val x: Boolean) : ModulePort()
                class Top : ModuleInterface() {
                    private var x = false
                    @Make
                    val mp = MP(x)
                }
            """.trimIndent()
        )
        val expected = """
            interface Top;
            
                logic x = 1'b0;
            
                modport mp (
                    input x
                );
            
            endinterface : Top
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize clocking block instantiation`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                class CB(override val event: Event, @In val x: Boolean) : ClockingBlock()
                class Top : Module() {
                    private var x = false
                    @Make
                    val cb = CB(posedge(x), x)
                }
            """.trimIndent()
        )
        val expected = """
            module Top;
            
                logic x = 1'b0;
            
                clocking cb @(posedge x);
                    input x;
                endclocking
            
            endmodule : Top
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize value parameter`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                fun f(x: Int) {}
            """.trimIndent()
        )
        val expected = """
            function automatic void f(int x);
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }
}
