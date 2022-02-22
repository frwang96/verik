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

class C0;

    function new();
    endfunction : new

    static logic x5 = 1'b0;

endclass : C0

class C1 extends C0;

    function new();
        super.new();
    endfunction : new

endclass : C1

class C2;

    static logic x6 = 1'b0;

endclass : C2

class C2_T_Int;

    function new();
    endfunction : new

endclass : C2_T_Int

class C3 extends C0;

    function new();
        super.new();
    endfunction : new

endclass : C3

class C4;

    logic x8 = 1'b0;

    function new(
        input logic x7
    );
        x8 = x7;
    endfunction : new

endclass : C4

module M;

    C0       x0 = C0::new();
    C1       x1 = C1::new();
    C2_T_Int x2 = C2_T_Int::new();
    C3       x3 = C3::new();
    C4       x4 = C4::new(.x7(1'b0));

    initial begin : f0
        $display($sformatf("%b", C0::x5));
        $display($sformatf("%b", C2::x6));
    end : f0

endmodule : M
