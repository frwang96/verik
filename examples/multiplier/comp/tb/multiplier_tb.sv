`timescale 1ns / 1ns

module multiplier_tb;

    logic        clk;
    logic        rst;
    logic [7:0]  in_a;
    logic [7:0]  in_b;
    logic        in_vld;
    logic [15:0] res;
    logic        res_rdy;
    logic [15:0] expected;

    multiplier multiplier (
        .clk     (clk),
        .rst     (rst),
        .in_a    (in_a),
        .in_b    (in_b),
        .in_vld  (in_vld),
        .res     (res),
        .res_rdy (res_rdy)
    );

    initial begin: toggle_clk
        clk = 1'b0;
        forever begin
            #10;
            clk = !clk;
        end
    end: toggle_clk

    initial begin: toggle_rst
        rst = 1'b1;
        @(negedge clk);
        rst = 1'b0;
        #1000;
        $finish();
    end: toggle_rst

    initial begin: test_gen
        in_a = 8'h00;
        in_b = 8'h00;
        expected = 16'h0000;
        #20;
        forever begin
            @(negedge clk);
            if (res_rdy) begin
                if (res == expected) begin
                    $display($sformatf("PASSED %0d * %0d test function gave %0d", in_a, in_b, res));
                end
                else begin
                    $display($sformatf("FAILED %0d * %0d test function gave %0d instead of %0d", in_a, in_b, res,
                        expected));
                end
                in_a = $random();
                in_b = $random();
                in_vld = 1'b1;
                expected = in_a * in_b;
            end
            else begin
                in_vld = 1'b0;
            end
        end
    end: test_gen

endmodule: multiplier_tb
