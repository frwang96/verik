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

    static function automatic C0 __new();
        C0 __0;
        __0 = new();
        __0.__init();
        return __0;
    endfunction : __new

    function automatic void __init();
    endfunction : __init

    static logic x4 = 1'b0;

endclass : C0

class C1 extends C0;

    static function automatic C1 __new();
        C1 __1;
        __1 = new();
        __1.__init();
        return __1;
    endfunction : __new

    function automatic void __init();
        super.__init();
    endfunction : __init

endclass : C1

class C2;

    static logic x5 = 1'b0;

endclass : C2

class C2_T_Int;

    static function automatic C2_T_Int __new();
        C2_T_Int __2;
        __2 = new();
        __2.__init();
        return __2;
    endfunction : __new

    function automatic void __init();
    endfunction : __init

endclass : C2_T_Int

module M;

    C0       x0 = C0::__new();
    C1       x1 = C1::__new();
    C2_T_Int x2 = C2_T_Int::__new();

    initial begin : f0
        $display($sformatf("%b", C0::x4));
        $display($sformatf("%b", C2::x5));
    end : f0

endmodule : M
