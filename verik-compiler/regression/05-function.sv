/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module M;

    function automatic logic f0();
        return 1'b0;
    endfunction : f0

    function automatic logic [7:0] f1(
        input logic [7:0] x
    );
        return ~x;
    endfunction : f1

    function automatic void f2(
        input int x = 0,
        input int y = 0
    );
    endfunction : f2

    initial begin : f3
        $display($sformatf("%b", f0()));
        $display($sformatf("0x%h", f1(.x(8'h00))));
        f2(.x(0), .y(0));
        f2(.x(0), .y());
        f2(.x(), .y());
    end : f3

endmodule : M
