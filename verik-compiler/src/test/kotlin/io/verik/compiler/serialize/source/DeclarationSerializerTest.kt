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
    fun `serialize type definition`() {
        driveTest(
            """
                fun f(x: Unpacked<`8`, Boolean>): Unpacked<`8`, Boolean> {
                    return x
                }
            """.trimIndent(),
            """
                typedef logic _${'$'}0 [7:0];
                
                function automatic _${'$'}0 f(input logic x [7:0]);
                    return x;
                endfunction : f
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize module simple`() {
        driveTest(
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
    fun `serialize module with port`() {
        driveTest(
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent(),
            """
                module M(
                    input logic x
                );
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize module interface simple`() {
        driveTest(
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
    fun `serialize class`() {
        driveTest(
            """
                class C
            """.trimIndent(),
            """
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize enum`() {
        driveTest(
            """
                enum class E { A, B }
            """.trimIndent(),
            """
                typedef enum {
                    A,
                    B
                } E;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize struct`() {
        driveTest(
            """
                class S(val x: Boolean) : Struct()
            """.trimIndent(),
            """
                typedef struct packed {
                    logic x;
                } S;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize function`() {
        driveTest(
            """
                fun f() {}
            """.trimIndent(),
            """
                function automatic void f();
                endfunction : f
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize task`() {
        driveTest(
            """
                @Task
                fun t(x: Int) {}
            """.trimIndent(),
            """
                task automatic t(input int x);
                endtask : t
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize property`() {
        driveTest(
            """
                var x = false
            """.trimIndent(),
            """
                logic x = 1'b0;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize initial block`() {
        driveTest(
            """
                class M : Module() {
                    @Run
                    fun f() {}
                }
            """.trimIndent(),
            """
                module M;
                
                    initial begin : f
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize always com block`() {
        driveTest(
            """
                class M : Module() {
                    @Com
                    fun f() {}
                }
            """.trimIndent(),
            """
                module M;
                
                    always_comb begin : f
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize always seq block`() {
        driveTest(
            """
                class M : Module() {
                    private var x : Boolean = nc()
                    @Seq
                    fun f() {
                        on (posedge(x)) {}
                    }
                }
            """.trimIndent(),
            """
                module M;
                
                    logic x;
                
                    always_ff @(posedge x) begin : f
                    end : f
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize module instantiation`() {
        driveTest(
            """
                class M(@In var x: Boolean): Module()
                class Top : Module() {
                    @Make
                    val m = M(false)
                }
            """.trimIndent(),
            """
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
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize module port instantiation`() {
        driveTest(
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
                        input x
                    );
                
                endinterface : Top
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize clocking block instantiation`() {
        driveTest(
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
                        input x;
                    endclocking
                
                endmodule : Top
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `serialize value parameter`() {
        driveTest(
            """
                fun f(x: Int) {}
            """.trimIndent(),
            """
                function automatic void f(input int x);
                endfunction : f
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }
}
