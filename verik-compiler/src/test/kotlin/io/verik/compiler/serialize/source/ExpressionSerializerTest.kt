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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `scope expression`() {
        driveTextFileTest(
            """
                var a = ArrayList<Boolean>()
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(logic) a = verik_pkg::ArrayList#(logic)::__new();
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `this expression`() {
        driveTextFileTest(
            """
                class C {
                    private var x = 0
                    fun f() {
                        println(this.x)
                    }
                }
            """.trimIndent(),
            """
                class C;
                
                    static function automatic C __new();
                        C __0;
                        __0 = new();
                        __0.__init();
                        return __0;
                    endfunction : __new
                
                    function automatic void __init();
                    endfunction : __init
                
                    int x = 0;
                
                    virtual function automatic void f();
                        ${'$'}display(${'$'}sformatf("%0d", this.x));
                    endfunction : f
                
                endclass : C
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `injected statement simple`() {
        driveTextFileTest(
            """
                var x = false
                fun f() {
                    sv("x <= !x;")
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    x <= !x;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `injected statement multiline`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                fun f() {
                    sv(${"\"\"\""}
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
                    for (int i = 0; i < 8; i++) begin
                    end
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
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
        ) { it.regularPackageTextFiles[0] }
    }
}
