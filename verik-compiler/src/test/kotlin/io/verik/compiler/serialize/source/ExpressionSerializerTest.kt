/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ExpressionSerializerTest : BaseTest() {

    @Test
    fun `parenthesized expression`() {
        driveTextFileTest(
            """
                var x = 0
                var y = (x + 1) * x
            """.trimIndent(),
            """
                int x = 0;
                int y = (x + 1) * x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `unary expression prefix`() {
        driveTextFileTest(
            """
                var x = false
                var y = !x
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = !x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `unary expression postfix`() {
        driveTextFileTest(
            """
                var x = 0
                var y = x++
            """.trimIndent(),
            """
                int x = 0;
                int y = x++;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `binary expression`() {
        driveTextFileTest(
            """
                var x = 0
                var y = x + 1
            """.trimIndent(),
            """
                int x = 0;
                int y = x + 1;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `reference expression`() {
        driveTextFileTest(
            """
                var x = 0
                var y = x
            """.trimIndent(),
            """
                int x = 0;
                int y = x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `call expression`() {
        driveTextFileTest(
            """
                var x = random()
            """.trimIndent(),
            """
                int x = ${"$"}random();
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `call expression default argument`() {
        driveTextFileTest(
            """
                fun f(x: Int = 0): Int { return x }
                var x = f()
            """.trimIndent(),
            """
                function automatic int f(
                    input int x = 0
                );
                    return x;
                endfunction : f

                int x = f(.x());
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `scope expression`() {
        driveTextFileTest(
            """
                var a = ArrayList<Boolean>()
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(.E(logic)) a = verik_pkg::ArrayList#(.E(logic))::__new();
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `constant expression`() {
        driveTextFileTest(
            """
                var x = 0
            """.trimIndent(),
            """
                int x = 0;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `struct literal expression`() {
        driveTextFileTest(
            """
                class S(val x: Boolean) : Struct()
                var s = S(false)
            """.trimIndent(),
            """
                typedef struct packed {
                    logic x;
                } S;
                
                S s = '{x:1'b0};
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `this expression`() {
        driveTextFileTest(
            """
                class C : Class() {
                    private var x = 0
                    fun f() {
                        println(this.x)
                    }
                }
            """.trimIndent(),
            """
                class C;
                
                    function new();
                    endfunction : new
                
                    int x = 0;
                
                    virtual function automatic void f();
                        ${'$'}display(${'$'}sformatf("%0d", this.x));
                    endfunction : f
                
                endclass : C
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `break statement`() {
        driveTextFileTest(
            """
                fun f() {
                    for (i in 0 until 1) { break }
                }
            """.trimIndent(),
            """
                function automatic void f();
                    for (int __0 = 0; __0 < 1; __0++) begin
                        int i;
                        i = __0;
                        break;
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `return statement`() {
        driveTextFileTest(
            """
                fun f() {
                    return
                }
            """.trimIndent(),
            """
                function automatic void f();
                    return;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `injected expression simple`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    inj("x <= !x;")
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    x <= !x;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `injected expression multiline`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                fun f() {
                    inj(${"\"\"\""}
                        x <= !x;
                        y <= !y;
                    ${"\"\"\""}.trimIndent())
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                
                function automatic void f();
                    x <= !x;
                    y <= !y;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `string expression`() {
        driveTextFileTest(
            """
                var x = "abc"
            """.trimIndent(),
            """
                string x = "abc";
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `array access expression`() {
        driveTextFileTest(
            """
                var x = u(0x00)
                var y = x[0]
            """.trimIndent(),
            """
                logic [7:0] x = 8'h00;
                logic       y = x[0];
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `concatenation expression`() {
        driveTextFileTest(
            """
                var x = cat(u(0), u(0))
            """.trimIndent(),
            """
                logic [1:0] x = { 1'b0, 1'b0 };
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `replication expression`() {
        driveTextFileTest(
            """
                var x = rep<`3`>(false)
            """.trimIndent(),
            """
                logic [2:0] x = {3{ 1'b0 }};
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `streaming expression`() {
        driveTextFileTest(
            """
                var x = u(0x00).rev()
            """.trimIndent(),
            """
                logic [7:0] x = {<<{ 8'h00 }};
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `width cast expression`() {
        driveTextFileTest(
            """
                var x = u(0x0).ext<`8`>()
            """.trimIndent(),
            """
                logic [7:0] x = 8'(4'b0000);
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `if expression`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    if (x) println() else println()
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    if (x) begin
                        ${'$'}display();
                    end
                    else begin
                        ${'$'}display();
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `if expression no then`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    if (x);
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    if (x);
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `if expression nested`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    @Suppress("CascadeIf")
                    if (x) {
                        println()
                    } else if (x) {
                        println()
                    } else {
                        println()
                    }
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    if (x) begin
                        ${'$'}display();
                    end
                    else if (x) begin
                        ${'$'}display();
                    end
                    else begin
                        ${'$'}display();
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression`() {
        driveTextFileTest(
            """
                var x = false
                var y = if (x) 0 else 1
            """.trimIndent(),
            """
                logic x = 1'b0;
                int   y = x ? 0 : 1;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `immediate assert statement`() {
        driveTextFileTest(
            """
                fun f() {
                    assert(true) { println() }
                }
            """.trimIndent(),
            """
                function automatic void f();
                    assert (1'b1) else begin
                        ${'$'}display();
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `case statement`() {
        driveTextFileTest(
            """
                var x = 0
                fun f() {
                    when (x) {
                        0 -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            """
                int x = 0;
                
                function automatic void f();
                    case (x)
                        0 : begin
                        end
                        default : begin
                        end
                    endcase
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `while expression`() {
        driveTextFileTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    while (true) {}
                }
            """.trimIndent(),
            """
                function automatic void f();
                    while (1'b1) begin
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `do while expression`() {
        driveTextFileTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    do {} while (true)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    do begin
                    end
                    while (1'b1);
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `for statement`() {
        driveTextFileTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    for (i in 0 until 8) {}
                }
            """.trimIndent(),
            """
                function automatic void f();
                    for (int __0 = 0; __0 < 8; __0++) begin
                        int i;
                        i = __0;
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `forever statement`() {
        driveTextFileTest(
            """
                fun f() {
                    forever {}
                }
            """.trimIndent(),
            """
                function automatic void f();
                    forever begin
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `repeat statement`() {
        driveTextFileTest(
            """
                fun f() {
                    repeat(3) {}
                }
            """.trimIndent(),
            """
                function automatic void f();
                    repeat (3) begin
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `fork statement`() {
        driveTextFileTest(
            """
                @Task
                fun f() {
                    fork { delay(10) }
                }
            """.trimIndent(),
            """
                task automatic f();
                    fork
                        begin
                            #10;
                        end
                    join_none
                endtask : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `wait fork statement`() {
        driveTextFileTest(
            """
                @Task
                fun f() {
                    join()
                }
            """.trimIndent(),
            """
                task automatic f();
                    wait fork;
                endtask : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `event control expression`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    wait(posedge(x))
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    @(posedge x);
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `delay expression`() {
        driveTextFileTest(
            """
                fun f() {
                    delay(1)
                }
            """.trimIndent(),
            """
                function automatic void f();
                    #1;
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
