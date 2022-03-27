/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

function int f0;
    input x0;
endfunction

class c;

    function logic f1;
        input int x1;
    endfunction

    function void f2(logic [3:0] x2, logic x3 = 1'b1);
    endfunction

    extern function logic [3:0] f3(int x4, int x5);

endclass