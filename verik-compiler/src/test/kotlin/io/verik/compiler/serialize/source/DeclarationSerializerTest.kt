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

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class DeclarationSerializerTest : BaseTest() {

    @Test
    fun `type definition`() {
        driveTextFileTest(
            """
                fun f(x: Unpacked<`8`, Boolean>): Unpacked<`8`, Boolean> {
                    return x
                }
            """.trimIndent(),
            """
                typedef logic __0 [7:0];
                
                function automatic __0 f(
                    input logic x [7:0]
                );
                    return x;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `injected property`() {
        driveTextFileTest(
            """
                @Inj
                val M = ${"\"\"\""}
                    module M;
                    endmodule
                ${"\"\"\""}.trimIndent()
            """.trimIndent(),
            """
                module M;
                endmodule
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module simple`() {
        driveTextFileTest(
            """
                class M: Module()
            """.trimIndent(),
            """
                module M;
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module with port`() {
        driveTextFileTest(
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent(),
            """
                module M(
                    input  logic x
                );
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module with documentation`() {
        driveTextFileTest(
            """
                /**
                 * ABC
                 */
                class M: Module()
            """.trimIndent(),
            """
                /**
                 * ABC
                 */
                module M;
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module interface simple`() {
        driveTextFileTest(
            """
                class MI: ModuleInterface()
            """.trimIndent(),
            """
                interface MI;
                
                endinterface : MI
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `class simple`() {
        driveTextFileTest(
            """
                class C
            """.trimIndent(),
            """
                class C;
                
                    function new();
                    endfunction : new
                
                endclass : C
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `enum simple`() {
        driveTextFileTest(
            """
                enum class E { A, B }
            """.trimIndent(),
            """
                typedef enum {
                    A,
                    B
                } E;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `struct simple`() {
        driveTextFileTest(
            """
                class S(val x: Boolean) : Struct()
            """.trimIndent(),
            """
                typedef struct packed {
                    logic x;
                } S;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `function simple`() {
        driveTextFileTest(
            """
                fun f() {}
            """.trimIndent(),
            """
                function automatic void f();
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `function default argument`() {
        driveTextFileTest(
            """
                fun f(x: Int = 0) {}
            """.trimIndent(),
            """
                function automatic void f(
                    input int x = 0
                );
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `task simple`() {
        driveTextFileTest(
            """
                @Task
                fun t(x: Int) {}
            """.trimIndent(),
            """
                task automatic t(
                    input int x
                );
                endtask : t
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `property simple`() {
        driveTextFileTest(
            """
                var x = false
            """.trimIndent(),
            """
                logic x = 1'b0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `property static`() {
        driveTextFileTest(
            """
                object O {
                    var x = false
                }
            """.trimIndent(),
            """
                class O;

                    static logic x = 1'b0;

                endclass : O
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `property with documentation`() {
        driveTextFileTest(
            """
                /**
                 * ABC
                 */
                var x = false
            """.trimIndent(),
            """
                /**
                 * ABC
                 */
                logic x = 1'b0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `initial block`() {
        driveTextFileTest(
            """
                class M : Module() {
                    @Run
                    fun f() { println() }
                }
            """.trimIndent(),
            """
                module M;
                
                    initial begin : f
                        ${'$'}display();
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `always com block`() {
        driveTextFileTest(
            """
                class M : Module() {
                    @Suppress("MemberVisibilityCanBePrivate")
                    var x: Boolean = nc()
                    @Com
                    fun f() { x = false }
                }
            """.trimIndent(),
            """
                module M;
                
                    logic x;
                
                    always_comb begin : f
                        x = 1'b0;
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `always seq block`() {
        driveTextFileTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class M : Module() {
                    var x : Boolean = nc()
                    var y : Boolean = nc()
                    @Seq
                    fun f() {
                        on (posedge(x)) { y = !y }
                    }
                }
            """.trimIndent(),
            """
                module M;
                
                    logic x;
                    logic y;
                
                    always_ff @(posedge x) begin : f
                        y <= !y;
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module instantiation`() {
        driveTextFileTest(
            """
                class M(@In var x: Boolean): Module()
                class Top : Module() {
                    @Make
                    val m = M(false)
                }
            """.trimIndent(),
            """
                module M(
                    input  logic x
                );
                
                endmodule : M
                
                module Top;
                
                    M m (
                        .x ( 1'b0 )
                    );
                
                endmodule : Top
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `module port instantiation`() {
        driveTextFileTest(
            """
                class MP(@In var x: Boolean) : ModulePort()
                class Top : ModuleInterface() {
                    private var x : Boolean = nc()
                    @Make
                    val mp = MP(x)
                }
            """.trimIndent(),
            """
                interface Top;
                
                    logic x;
                
                    modport mp (
                        input  x
                    );
                
                endinterface : Top
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `clocking block instantiation`() {
        driveTextFileTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class Top : Module() {
                    private var x : Boolean = nc()
                    @Make
                    val cb = CB(posedge(x), x)
                }
            """.trimIndent(),
            """
                module Top;
                
                    logic x;
                
                    clocking cb @(posedge x);
                        input  x;
                    endclocking
                
                endmodule : Top
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }
}
