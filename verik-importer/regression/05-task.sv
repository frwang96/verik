/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

task t0;
    input x0;
endtask

class c;

    task t1;
        input int x1;
    endtask

    task t2(logic [3:0] x2, logic x3);
    endtask

    extern task t3(int x4, int x5);

endclass