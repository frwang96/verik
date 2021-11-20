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
            """
                fun f(x: Unpacked<`8`, Boolean>): Unpacked<`8`, Boolean> {
                    return x
                }
            """.trimIndent()
        )
        val expected = """
            typedef logic _${'$'}0 [7:0];
            
            function automatic _${'$'}0 f(input logic x [7:0]);
                return x;
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize module simple`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize module with port`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize module interface simple`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize class`() {
        val projectContext = driveTest(
            """
                class C
            """.trimIndent()
        )
        val expected = """
            class C;
            
                static function automatic C _${'$'}new();
                    automatic C _${'$'}0 = new();
                    _${'$'}0._${'$'}init();
                    return _${'$'}0;
                endfunction : _${'$'}new
            
                function automatic void _${'$'}init();
                endfunction : _${'$'}init
            
            endclass : C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize enum`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize struct`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize function`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize task`() {
        val projectContext = driveTest(
            """
                @Task
                fun t(x: Int) {}
            """.trimIndent()
        )
        val expected = """
            task automatic t(input int x);
            endtask : t
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize property`() {
        val projectContext = driveTest(
            """
                var x = false
            """.trimIndent()
        )
        val expected = """
            logic x = 1'b0;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize initial block`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize always com block`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize always seq block`() {
        val projectContext = driveTest(
            """
                class M : Module() {
                    private var x : Boolean = nc()
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent()
        )
        val expected = """
            module M;
            
                logic x;
            
                always_ff @(posedge x) begin : f
                end : f
            
            endmodule : M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize module instantiation`() {
        val projectContext = driveTest(
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
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize module port instantiation`() {
        val projectContext = driveTest(
            """
                class MP(@In var x: Boolean) : ModulePort()
                class Top : ModuleInterface() {
                    private var x : Boolean = nc()
                    @Make
                    val mp = MP(x)
                }
            """.trimIndent()
        )
        val expected = """
            interface Top;
            
                logic x;
            
                modport mp (
                    input x
                );
            
            endinterface : Top
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize clocking block instantiation`() {
        val projectContext = driveTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class Top : Module() {
                    private var x : Boolean = nc()
                    @Make
                    val cb = CB(posedge(x), x)
                }
            """.trimIndent()
        )
        val expected = """
            module Top;
            
                logic x;
            
                clocking cb @(posedge x);
                    input x;
                endclocking
            
            endmodule : Top
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.rootPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize value parameter`() {
        val projectContext = driveTest(
            """
                fun f(x: Int) {}
            """.trimIndent()
        )
        val expected = """
            function automatic void f(input int x);
            endfunction : f
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }
}
