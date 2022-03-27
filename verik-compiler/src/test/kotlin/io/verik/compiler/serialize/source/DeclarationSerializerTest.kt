/*
 * SPDX-License-Identifier: Apache-2.0
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
                val x = ${"\"\"\""}
                    abc
                ${"\"\"\""}.trimIndent()
            """.trimIndent(),
            """
                abc
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `class simple`() {
        driveTextFileTest(
            """
                class C : Class()
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
    fun `cover group simple`() {
        driveTextFileTest(
            """
                class CG(@In var x: Boolean) : CoverGroup()
            """.trimIndent(),
            """
                covergroup CG(
                    ref logic x
                );
                
                endgroup : CG
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
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
    fun `enum with property`() {
        driveTextFileTest(
            """
                enum class E(val value: Ubit<`4`>) { A(u(0x0)) }
            """.trimIndent(),
            """
                typedef enum logic [3:0] {
                    A = 4'b0000
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
    fun `union simple`() {
        driveTextFileTest(
            """
                class U(var x: Boolean, var y: Ubit<`1`>): Union()
            """.trimIndent(),
            """
                typedef union packed {
                    logic       x;
                    logic [0:0] y;
                } U;
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
                object O : Class() {
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
    fun `property rand`() {
        driveTextFileTest(
            """
                class C : Class() {
                    @Rand
                    var x = false
                }
            """.trimIndent(),
            """
                class C;

                    function new();
                    endfunction : new

                    rand logic x = 1'b0;

                endclass : C
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
    fun `cover point simple`() {
        driveTextFileTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                }
            """.trimIndent(),
            """
                covergroup CG(
                    ref logic x
                );

                    cp : coverpoint x;

                endgroup : CG
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `cover point with cover bin`() {
        driveTextFileTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x) {
                        bin("b", "{1'b0}")
                    }
                }
            """.trimIndent(),
            """
                covergroup CG(
                    ref logic x
                );

                    cp : coverpoint x {

                        bins b = {1'b0};

                    }

                endgroup : CG
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `cover cross simple`() {
        driveTextFileTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp, cp)
                }
            """.trimIndent(),
            """
                covergroup CG(
                    ref logic x
                );

                    cp : coverpoint x;
                
                    cc : cross cp, cp;

                endgroup : CG
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `cover cross with cover bins`() {
        driveTextFileTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp, cp) {
                        bins("b", "binsof(cp)")
                    }
                }
            """.trimIndent(),
            """
                covergroup CG(
                    ref logic x
                );

                    cp : coverpoint x;
                
                    cc : cross cp, cp {

                        bins b[] = binsof(cp);

                    }

                endgroup : CG
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
                class M : ModuleInterface() {
                    private var x : Boolean = nc()
                    @Make
                    val mp = MP(x)
                }
            """.trimIndent(),
            """
                interface M;
                
                    logic x;
                
                    modport mp (
                        input  x
                    );
                
                endinterface : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `clocking block instantiation`() {
        driveTextFileTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class M : Module() {
                    private var x : Boolean = nc()
                    @Make
                    val cb = CB(posedge(x), x)
                }
            """.trimIndent(),
            """
                module M;
                
                    logic x;
                
                    clocking cb @(posedge x);
                        input  x;
                    endclocking
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `constraint simple`() {
        driveTextFileTest(
            """
                class C : Class(){
                    var x = 0
                    @Cons
                    var c = c(x == 0)
                }
            """.trimIndent(),
            """
                class C;

                    function new();
                    endfunction : new

                    int x = 0;

                    constraint c {
                        x == 0;
                    }

                endclass : C
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `cluster simple`() {
        driveTextFileTest(
            """
                val x = cluster(2) { it }
            """.trimIndent(),
            """
                int x_0 = 0;
                int x_1 = 1;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
