/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

typedef enum {
    E0,
    E1,
    E2
} E;

module M;

    E e;

    initial begin : f
        if (e == E0) begin
            $display("E1");
        end
    end : f

endmodule : M
