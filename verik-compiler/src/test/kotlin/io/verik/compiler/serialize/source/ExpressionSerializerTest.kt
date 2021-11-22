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

internal class ExpressionSerializerTest : BaseTest() {

    @Test
    fun `parenthesized expression`() {
        driveTest(
            """
                var x = 0
                var y = (x + 1) * x
            """.trimIndent(),
            """
                int x = 0;
                int y = (x + 1) * x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `unary expression prefix`() {
        driveTest(
            """
                var x = false
                var y = !x
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = !x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `unary expression postfix`() {
        driveTest(
            """
                var x = 0
                var y = x++
            """.trimIndent(),
            """
                int x = 0;
                int y = x++;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `binary expression`() {
        driveTest(
            """
                var x = 0
                var y = x + 1
            """.trimIndent(),
            """
                int x = 0;
                int y = x + 1;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `reference expression`() {
        driveTest(
            """
                var x = 0
                var y = x
            """.trimIndent(),
            """
                int x = 0;
                int y = x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `call expression`() {
        driveTest(
            """
                var x = random()
            """.trimIndent(),
            """
                int x = ${"$"}random();
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `scope expression`() {
        driveTest(
            """
                var a = ArrayList<Boolean>()
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(logic) a = verik_pkg::ArrayList#(logic)::_${'$'}new();
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `constant expression`() {
        driveTest(
            """
                var x = 0
            """.trimIndent(),
            """
                int x = 0;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `struct literal expression`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `this expression`() {
        driveTest(
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
                
                    static function automatic C _${'$'}new();
                        automatic C _${'$'}0 = new();
                        _${'$'}0._${'$'}init();
                        return _${'$'}0;
                    endfunction : _${'$'}new
                
                    function automatic void _${'$'}init();
                    endfunction : _${'$'}init
                
                    int x = 0;
                
                    virtual function automatic void f();
                        ${'$'}display(this.x);
                    endfunction : f
                
                endclass : C
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `return statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `injected statement`() {
        driveTest(
            """
                var x = 0
                fun f() {
                    sv("${"$"}x <= #1 !${"$"}x")
                }
            """.trimIndent(),
            """
                int x = 0;
                
                function automatic void f();
                    x <= #1 !x;
                endfunction : f
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `string expression`() {
        driveTest(
            """
                var x = "abc"
            """.trimIndent(),
            """
                string x = "abc";
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `array access expression`() {
        driveTest(
            """
                var x = u(0x00)
                var y = x[0]
            """.trimIndent(),
            """
                logic [7:0] x = 8'h00;
                logic       y = x[0];
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `concatenation expression`() {
        driveTest(
            """
                var x = cat(u(0), u(0))
            """.trimIndent(),
            """
                logic [1:0] x = { 1'h0, 1'h0 };
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `replication expression`() {
        driveTest(
            """
                var x = rep<`3`>(false)
            """.trimIndent(),
            """
                logic [2:0] x = {3{ 1'b0 }};
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `streaming expression`() {
        driveTest(
            """
                var x = u(0x00).reverse()
            """.trimIndent(),
            """
                logic [7:0] x = {<<{ 8'h00 }};
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `width cast expression`() {
        driveTest(
            """
                var x = u(0x0).ext<`8`>()
            """.trimIndent(),
            """
                logic [7:0] x = 8'(4'h0);
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `if expression`() {
        driveTest(
            """
                var x = false
                fun f() {
                    if (x) 1 else 0
                }
            """.trimIndent(),
            """
                logic x = 1'b0;
                
                function automatic void f();
                    if (x)
                        1;
                    else
                        0;
                endfunction : f
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `if expression no then`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `if expression nested`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression`() {
        driveTest(
            """
                var x = false
                var y = if (x) 0 else 1
            """.trimIndent(),
            """
                logic x = 1'b0;
                int   y = x ? 0 : 1;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `immediate assert statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `case statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `while expression`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `do while expression`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `for statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `forever statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `repeat statement`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `event control expression`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `delay expression`() {
        driveTest(
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
        ) { it.basicPackageTextFiles[0] }
    }
}
