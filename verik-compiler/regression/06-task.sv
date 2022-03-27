/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module M;

    task automatic f0();
        #1;
    endtask : f0

    task automatic f1(
        input logic [7:0]  x,
        output logic [7:0] __0
    );
        #1;
        __0 = ~x;
        return;
    endtask : f1

    initial begin : f2
        logic [7:0] __1;
        logic [7:0] x;
        f0();
        f1(.x(8'h00), .__0(__1));
        x = __1;
        $display($sformatf("0x%h", x));
    end : f2

endmodule : M
