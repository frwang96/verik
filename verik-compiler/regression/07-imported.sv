/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module M;

    initial begin : f
        logic                   x1;
        C0                      c0;
        test_pkg::C1 #(.T(int)) c1;
        logic                   x2;
        x1 = x0;
        c0 = C0::new();
        c1 = test_pkg::C1 #(.T(int))::new();
        x2 = test_pkg::C1 #(.T(int))::f0();
    end : f

endmodule : M
