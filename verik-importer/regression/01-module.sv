/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module m0;
endmodule

module m1(
    input logic x0,
    input logic [7:0] x1,
    output logic [3:0][1:0] x2
);
endmodule

module m2(x3, x4);
    input x3;
    input [7:0] x4;
endmodule
