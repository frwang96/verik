/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module M;

    logic       x0;
    logic [7:0] x1;
    logic [7:0] x2;

    initial begin : f0
        $display();
    end : f0

    logic x3;

    always_comb begin : __0
        x3 = !x0;
    end : __0

    logic x4;

    always_comb begin : f1
        x4 = !x0;
    end : f1

    logic [7:0] x5;

    always_ff @(posedge x0) begin : __1
        x5 <= x1;
    end : __1

    always_ff @(posedge x0) begin : f2
        x2 <= x1;
    end : f2

endmodule : M
