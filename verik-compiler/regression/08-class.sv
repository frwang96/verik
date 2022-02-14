/*
 * Copyright (c) 2022 Francis Wang
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

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

`ifndef VERIK
`define VERIK
`timescale 1ns / 1ns
`endif

class C0;

    function new();
    endfunction : new

    static logic x4 = 1'b0;

endclass : C0

class C1 extends C0;

    function new();
        super.new();
    endfunction : new

endclass : C1

class C2;

    static logic x5 = 1'b0;

endclass : C2

class C2_T_Int;

    function new();
    endfunction : new

endclass : C2_T_Int

module M;

    C0       x0 = C0::new();
    C1       x1 = C1::new();
    C2_T_Int x2 = C2_T_Int::new();

    initial begin : f0
        $display($sformatf("%b", C0::x4));
        $display($sformatf("%b", C2::x5));
    end : f0

endmodule : M
