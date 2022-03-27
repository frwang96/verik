/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

class c0;
    function new(logic x0, logic x1);
    endfunction
endclass

class c1 extends c0;
    function new(logic x2, logic x3);
        super.new(x2, x3);
    endfunction
endclass
