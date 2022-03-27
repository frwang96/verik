/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

typedef struct packed {
    logic       x0;
    logic [7:0] x1;
} S;

module M;

    S s;

    initial begin : f
        s = '{x0:1'b0, x1:8'h00};
    end : f

endmodule : M
